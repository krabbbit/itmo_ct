//go:build !solution

package retryupdate

import (
	"errors"
	"github.com/gofrs/uuid"
	"gitlab.com/slon/shad-go/retryupdate/kvapi"
)

func UpdateValue(c kvapi.Client, key string, updateFn func(oldValue *string) (newValue string, err error)) error {
	var data *kvapi.GetResponse
	var err error
	var authError *kvapi.AuthError
	var confError *kvapi.ConflictError
	var newValue string
	var vers uuid.UUID

	for {
		for {
			data, err = c.Get(&kvapi.GetRequest{Key: key})
			if err != nil {
				if errors.Is(err, kvapi.ErrKeyNotFound) {
					break
				}
				if errors.As(err, &authError) {
					return err
				}
				continue
			}
			break
		}

		if data != nil {
			newValue, err = updateFn(&data.Value)
			vers = data.Version
		} else {
			newValue, err = updateFn(nil)
			vers = uuid.UUID{}
		}

		if err != nil {
			return err
		}

		newVers := uuid.Must(uuid.NewV4())
		for {
			_, err := c.Set(&kvapi.SetRequest{Key: key, Value: newValue, OldVersion: vers, NewVersion: newVers})

			if errors.As(err, &authError) {
				return err
			}

			if errors.Is(err, kvapi.ErrKeyNotFound) {
				newValue, err = updateFn(nil)
				vers = uuid.UUID{}
				if err != nil {
					return err
				}
				continue
			}

			if errors.As(err, &confError) {
				if confError.ExpectedVersion == newVers {
					return nil
				}

				var response *kvapi.GetResponse
				for {
					response, err = c.Get(&kvapi.GetRequest{Key: key})
					if err != nil {
						if errors.Is(err, kvapi.ErrKeyNotFound) {
							err = nil
							break
						}
						if errors.As(err, &authError) {
							return err
						}
						continue
					}
					break
				}
				if err != nil {
					return err
				}
				if response == nil {
					newValue, err = updateFn(nil)
					if err != nil {
						return err
					}
					vers = uuid.UUID{}
				} else {
					newValue, err = updateFn(&response.Value)
					if err != nil {
						return err
					}
					vers = response.Version
				}
				if err != nil {
					return err
				}
				continue
			}

			if err != nil {
				continue
			}
			return nil
		}
	}
}
