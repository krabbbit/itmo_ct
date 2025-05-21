//go:build !solution

package blowfish

// #cgo pkg-config: libcrypto
// #cgo CFLAGS: -Wno-deprecated-declarations
// #include <openssl/blowfish.h>
import "C"
import "unsafe"

type Blowfish struct {
	size  int
	bfKey C.BF_KEY
}

func (b Blowfish) BlockSize() int {
	return b.size
}

func (b Blowfish) base(dst, src []byte, t C.int) {
	C.BF_ecb_encrypt(
		(*C.uchar)(unsafe.Pointer(&src[0])),
		(*C.uchar)(unsafe.Pointer(&dst[0])),
		&b.bfKey,
		t)
}

func (b Blowfish) Encrypt(dst, src []byte) {
	b.base(dst, src, C.BF_ENCRYPT)
}

func (b Blowfish) Decrypt(dst, src []byte) {
	b.base(dst, src, C.BF_DECRYPT)
}

func New(key []byte) *Blowfish {
	bf := &Blowfish{}
	bf.size = 8
	C.BF_set_key(&bf.bfKey, C.int(len(key)), (*C.uchar)(unsafe.Pointer(&key[0])))
	return bf
}
