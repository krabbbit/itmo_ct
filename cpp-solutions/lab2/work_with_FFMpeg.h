#ifndef WORK_WITH_FFMPEG_H
#define WORK_WITH_FFMPEG_H

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>

#include <stdlib.h>	   //size_t

typedef struct
{
	int samp;
	size_t delta;
	AVCodecContext *ctx;
	AVFormatContext *pFormatCtx;
	AVPacket *packet;
	AVStream *audioStream;
	AVFrame *frame;
	const AVCodec *codec;
	size_t samples_count;
} data;

typedef struct
{
	size_t size;
	size_t this_index;
	double *array;
} my_array;

int init_data(data *props);
int init_array(my_array *array);
void free_resources(data *props1, data *props2, my_array *array1, my_array *array2);
int read_ffmpeg_files(int argc, char *argv[], my_array *array1, my_array *array2, data *props1, data *props2);
int print_res(int delta, int argc, data *props1, data *props2);

#endif
