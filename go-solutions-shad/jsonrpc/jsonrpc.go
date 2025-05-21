//go:build !solution

package jsonrpc

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"net/http"
	"reflect"
)

type request struct {
	Method string          `json:"method"`
	Params json.RawMessage `json:"params"`
}

type response struct {
	Result any    `json:"result,omitempty"`
	Error  string `json:"error,omitempty"`
}

type handler struct {
	service any
}

func (h *handler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	var req request
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, `{"error":"invalid request"}`, http.StatusBadRequest)
		return
	}
	defer r.Body.Close()

	methodValue := reflect.ValueOf(h.service).MethodByName(req.Method)
	if !methodValue.IsValid() {
		http.Error(w, `{"error":"method not found"}`, http.StatusNotFound)
		return
	}

	methodType := methodValue.Type()
	if methodType.NumIn() != 2 || methodType.NumOut() != 2 {
		http.Error(w, `{"error":"invalid method signature"}`, http.StatusBadRequest)
		return
	}

	paramType := methodType.In(1).Elem()
	params := reflect.New(paramType)
	if err := json.Unmarshal(req.Params, params.Interface()); err != nil {
		http.Error(w, `{"error":"invalid params"}`, http.StatusBadRequest)
		return
	}

	ctx := reflect.ValueOf(r.Context())
	results := methodValue.Call([]reflect.Value{ctx, params})

	resp := response{
		Result: results[0].Interface(),
	}
	if err, ok := results[1].Interface().(error); ok && err != nil {
		resp.Error = err.Error()
	}

	w.Header().Set("Content-Type", "application/json")
	_ = json.NewEncoder(w).Encode(resp)
}

func MakeHandler(service any) http.Handler {
	return &handler{service: service}
}

func Call(ctx context.Context, endpoint string, method string, req, rsp any) error {
	params, err := json.Marshal(req)
	if err != nil {
		return err
	}

	request := request{
		Method: method,
		Params: params,
	}

	requestBody, err := json.Marshal(request)
	if err != nil {
		return err
	}

	httpReq, err := http.NewRequestWithContext(ctx, "POST", endpoint, bytes.NewReader(requestBody))
	if err != nil {
		return err
	}
	httpReq.Header.Set("Content-Type", "application/json")

	httpResp, err := http.DefaultClient.Do(httpReq)
	if err != nil {
		return err
	}
	defer httpResp.Body.Close()

	var resp response
	if err := json.NewDecoder(httpResp.Body).Decode(&resp); err != nil {
		return err
	}

	if resp.Error != "" {
		return errors.New(resp.Error)
	}

	if rsp != nil {
		resultBytes, err := json.Marshal(resp.Result)
		if err != nil {
			return err
		}
		if err := json.Unmarshal(resultBytes, rsp); err != nil {
			return err
		}
	}

	return nil
}
