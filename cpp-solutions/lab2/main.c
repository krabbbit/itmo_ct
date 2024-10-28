#include "func_return_code.h"
#include "return_codes.h"
#include "work_with_FFMpeg.h"
#include "work_with_FFTW.h"

int main(int argc, char* argv[])
{
	int code = SUCCESS;

	data props1 = {};
	data props2 = {};
	my_array array1 = {};
	my_array array2 = {};

	code = init_data(&props1);
	RETURN_AND_FREE_FFMpeg(code);

	code = init_data(&props2);
	RETURN_AND_FREE_FFMpeg(code);

	code = init_array(&array1);
	RETURN_AND_FREE_FFMpeg(code);

	code = init_array(&array2);
	RETURN_AND_FREE_FFMpeg(code);

	code = read_ffmpeg_files(argc, argv, &array1, &array2, &props1, &props2);
	RETURN_AND_FREE_FFMpeg(code);

	int delta = 0;
	code = do_cross_corr(&array1, &array2, &delta);
	RETURN_AND_FREE_FFMpeg(code);

	code = print_res(delta, argc, &props1, &props2);
	RETURN_AND_FREE_FFMpeg(code);

lCleanup:
	free_resources(&props1, &props2, &array1, &array2);
	return code;
}
