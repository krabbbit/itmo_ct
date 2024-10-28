#include "work_with_FFTW.h"

#include "return_codes.h"
#include "work_with_FFMpeg.h"

#include <fftw3.h>
#include <stdio.h>	   //fputs
#include <string.h>	   //memset, etc

static void zero_padding(size_t res_size, my_array *array, double *real)
{
	memcpy(real, array->array, sizeof(double) * array->size);
	memset(real + array->size, 0, sizeof(double) * (res_size - array->size));
}

int do_cross_corr(my_array *array1, my_array *array2, int *delta)
{
	int result = SUCCESS;
	size_t res_size = array1->size + array2->size - 1;

	double *real_data = fftw_alloc_real(sizeof(double) * 3 * res_size);
	double *real_1 = real_data;
	double *real_2 = real_data + res_size;
	double *res_real = real_data + 2 * res_size;

	fftw_complex *complex_data = fftw_alloc_complex(sizeof(fftw_complex) * 3 * res_size);
	fftw_complex *out1 = complex_data;
	fftw_complex *out2 = complex_data + res_size;
	fftw_complex *res_out = complex_data + 2 * res_size;

	fftw_plan plan1 = NULL, plan2 = NULL, plan_res = NULL;

	if (!out1 || !out2 || !res_out || !real_1 || !real_2 || !res_real)
	{
		fputs("Error: unable to allocate FFTW resources\n", stderr);
		result = ERROR_NOTENOUGH_MEMORY;
		goto lCleanup;
	}

	zero_padding(res_size, array1, real_1);
	zero_padding(res_size, array2, real_2);

	plan1 = fftw_plan_dft_r2c_1d((int)res_size, real_1, out1, FFTW_ESTIMATE);
	plan2 = fftw_plan_dft_r2c_1d((int)res_size, real_2, out2, FFTW_ESTIMATE);

	if (!plan1 || !plan2)
	{
		fputs("Error: unable to create FFTW plan\n", stderr);
		result = ERROR_NOTENOUGH_MEMORY;
		goto lCleanup;
	}

	fftw_execute(plan1);
	fftw_execute(plan2);

	for (size_t i = 0; i < res_size; i++)
	{
		res_out[i][0] = out1[i][0] * out2[i][0] + out1[i][1] * out2[i][1];
		res_out[i][1] = -out1[i][0] * out2[i][1] + out1[i][1] * out2[i][0];
	}

	plan_res = fftw_plan_dft_c2r_1d((int)res_size, res_out, res_real, FFTW_ESTIMATE);
	if (!plan_res)
	{
		fputs("Error: unable to create inverse FFTW plan\n", stderr);
		result = ERROR_NOTENOUGH_MEMORY;
		goto lCleanup;
	}

	fftw_execute(plan_res);

	double mmax = 0;
	for (size_t i = 0; i < res_size; i++)
	{
		if (res_real[i] > mmax)
		{
			mmax = res_real[i];
			*delta = (int)i;
		}
	}

	if (*delta > array1->size)
	{
		*delta -= (int)res_size;
	}

lCleanup:
	if (real_data)
		fftw_free(real_data);
	if (complex_data)
		fftw_free(complex_data);

	if (plan1)
		fftw_destroy_plan(plan1);
	if (plan2)
		fftw_destroy_plan(plan2);
	if (plan_res)
		fftw_destroy_plan(plan_res);
	fftw_cleanup();
	return result;
}
