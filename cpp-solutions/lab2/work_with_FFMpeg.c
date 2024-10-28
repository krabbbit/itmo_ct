#include "work_with_FFMpeg.h"

#include "func_return_code.h"
#include "return_codes.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>

#include <stdio.h>	   //fputs
#include <stdlib.h>	   // malloc, etc
#include <string.h>	   //memset, etc

int init_data(data *props)
{
	props->pFormatCtx = avformat_alloc_context();
	props->frame = av_frame_alloc();
	props->packet = av_packet_alloc();
	props->samples_count = 0;
	props->samp = 0;
	props->ctx = NULL;

	if (!props->frame || !props->packet || !props->pFormatCtx)
	{
		fputs("Error: allocate memory for FFmpeg structures\n", stderr);
		return ERROR_NOTENOUGH_MEMORY;
	}

	return SUCCESS;
}

int init_array(my_array *array)
{
	array->size = 64;
	array->array = malloc(sizeof(double) * array->size);
	if (!array->array)
	{
		fputs("Error: allocate memory for array\n", stderr);
		return ERROR_NOTENOUGH_MEMORY;
	}
	memset(array->array, 0, sizeof(double) * array->size);
	array->this_index = 0;

	return SUCCESS;
}

static void free_memory(data *props, my_array *array)
{
	if (props->ctx)
	{
		avcodec_free_context(&props->ctx);
	}
	if (props->frame)
	{
		av_frame_free(&props->frame);
	}
	if (props->packet)
	{
		av_packet_free(&props->packet);
	}
	if (props->pFormatCtx)
	{
		avformat_close_input(&props->pFormatCtx);
	}
	if (array && array->array)
	{
		free(array->array);
	}
}

void free_resources(data *props1, data *props2, my_array *array1, my_array *array2)
{
	free_memory(props1, array1);
	free_memory(props2, array2);
}

static int add_to_array(my_array *array, double elem)
{
	if (array->this_index >= array->size)
	{
		array->size *= 2;
		double *new_array = realloc(array->array, sizeof(double) * array->size);
		if (!new_array)
		{
			fputs("Error reallocating memory\n", stderr);
			return ERROR_NOTENOUGH_MEMORY;
		}
		array->array = new_array;
		memset(array->array + array->size / 2, 0, sizeof(double) * array->size / 2);
	}
	array->array[array->this_index] = elem;
	array->this_index++;
	return SUCCESS;
}

static int send_packet(data *props)
{
	int code = avcodec_send_packet(props->ctx, props->packet);
	switch (code)
	{
	case AVERROR(EINVAL):
		fputs("Codec not open\n", stderr);
		return ERROR_CANNOT_OPEN_FILE;
	case AVERROR(EAGAIN):
		fputs("Try again, input is not accepted\n", stderr);
		return ERROR_UNSUPPORTED;
	case AVERROR_EOF:
		fputs("The decoder has been flushed\n", stderr);
		return ERROR_DATA_INVALID;
	case AVERROR(ENOMEM):
		fputs("Not enough memory\n", stderr);
		return ERROR_NOTENOUGH_MEMORY;
	default:
		if (code < 0)
		{
			fputs("Error sending a packet for decoding\n", stderr);
			return ERROR_DATA_INVALID;
		}
	}
	return SUCCESS;
}

static int decode_frame(data *props, my_array *array, int stream_num)
{
	int code = avcodec_receive_frame(props->ctx, props->frame);
	while (code != AVERROR(EAGAIN) && code != AVERROR_EOF)
	{
		switch (code)
		{
		case AVERROR(EINVAL):
			fputs("Codec not open\n", stderr);
			return ERROR_CANNOT_OPEN_FILE;
		default:
			if (code < 0)
			{
				fputs("Error during decoding\n", stderr);
				return ERROR_DATA_INVALID;
			}
		}
		props->samples_count += props->frame->nb_samples;
		for (size_t i = 0; i < props->frame->nb_samples; i++)
		{
			double tmp = (double)props->frame->data[stream_num][i];
			code = add_to_array(array, tmp);
			RETURN_FFMpeg(code);
		}
		code = avcodec_receive_frame(props->ctx, props->frame);
	}
	return SUCCESS;
}

static int add_samples(data *props, my_array *array, int stream_num)
{
	while (av_read_frame(props->pFormatCtx, props->packet) == 0)
	{
		if (props->packet->stream_index == props->audioStream->index)
		{
			int ret = send_packet(props);
			RETURN_FFMpeg(ret);
			ret = decode_frame(props, array, stream_num);
			RETURN_FFMpeg(ret);
		}
		av_packet_unref(props->packet);
	}
	return SUCCESS;
}

static int create_ffmpeg_props(char *argv[], data *props, int num_argv)
{
	int ret1 = avformat_open_input(&props->pFormatCtx, argv[num_argv], NULL, NULL);
	switch (ret1)
	{
	case AVERROR(EINVAL):
		fputs("Uncorrected parameters\n", stderr);
		return ERROR_CANNOT_OPEN_FILE;
	case AVERROR(ENOENT):
		fputs("File not found\n", stderr);
		return ERROR_ARGUMENTS_INVALID;
	case AVERROR(ENOMEM):
		fputs("Not enough memory\n", stderr);
		return ERROR_NOTENOUGH_MEMORY;
	case AVERROR(ENOSYS):
		fputs("Function not provided\n", stderr);
		return ERROR_UNKNOWN;
	case AVERROR(EIO):
		fputs("Can't read file\n", stderr);
		return ERROR_CANNOT_OPEN_FILE;
	case AVERROR_UNKNOWN:
		fputs("Unknown error\n", stderr);
		return ERROR_UNKNOWN;
	case AVERROR_INVALIDDATA:
		fputs("Invalid data in file\n", stderr);
		return ERROR_DATA_INVALID;
	default:
		if (ret1 < 0)
		{
			fputs("Error opening file\n", stderr);
			return ERROR_CANNOT_OPEN_FILE;
		}
	}

	if (avformat_find_stream_info(props->pFormatCtx, NULL) < 0)
	{
		fputs("Error: no audio stream information found\n", stderr);
		return ERROR_DATA_INVALID;
	}

	int index = av_find_best_stream(props->pFormatCtx, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
	if (index < 0)
	{
		fputs("Error: could not find audio stream\n", stderr);
		return ERROR_FORMAT_INVALID;
	}

	props->audioStream = props->pFormatCtx->streams[index];
	if (!props->audioStream)
	{
		fputs("Error: audio stream is NULL\n", stderr);
		return ERROR_FORMAT_INVALID;
	}

	enum AVCodecID id = props->audioStream->codecpar->codec_id;

	switch (id)
	{
	case AV_CODEC_ID_FLAC:
	case AV_CODEC_ID_MP2:
	case AV_CODEC_ID_MP3:
	case AV_CODEC_ID_OPUS:
	case AV_CODEC_ID_AAC:
		break;
	default:
		fputs("Incorrect type of file\n", stderr);
		return ERROR_DATA_INVALID;
	}

	props->codec = avcodec_find_decoder(id);
	if (!props->codec)
	{
		fputs("Failed to find codec for audio stream\n", stderr);
		return ERROR_UNSUPPORTED;
	}

	props->ctx = avcodec_alloc_context3(props->codec);
	if (!props->ctx)
	{
		fputs("Failed to allocate codec context\n", stderr);
		return ERROR_NOTENOUGH_MEMORY;
	}

	if (avcodec_parameters_to_context(props->ctx, props->audioStream->codecpar) < 0)
	{
		fputs("Failed to copy codec parameters to codec context\n", stderr);
		return ERROR_DATA_INVALID;
	}

	if (avcodec_open2(props->ctx, props->codec, NULL) < 0)
	{
		fputs("Failed to open codec\n", stderr);
		return ERROR_CANNOT_OPEN_FILE;
	}

	return SUCCESS;
}

int read_ffmpeg_files(int argc, char *argv[], my_array *array1, my_array *array2, data *props1, data *props2)
{
	av_log_set_level(AV_LOG_QUIET);
	if (argc != 2 && argc != 3)
	{
		fputs("Incorrect number of arguments cmd\n", stderr);
		return ERROR_FORMAT_INVALID;
	}
	RETURN_FFMpeg(create_ffmpeg_props(argv, props1, 1));
	RETURN_FFMpeg(add_samples(props1, array1, 0));

	if (argc == 3)
	{
		RETURN_FFMpeg(create_ffmpeg_props(argv, props2, 2));
		if (props1->ctx->ch_layout.nb_channels < 1)
		{
			fputs("Invalid chanels count in file1", stderr);
			return ERROR_FORMAT_INVALID;
		}

		if (props2->ctx->ch_layout.nb_channels < 1)
		{
			fputs("Invalid chanels count in file2", stderr);
			return ERROR_FORMAT_INVALID;
		}

		RETURN_FFMpeg(add_samples(props2, array2, 0));
	}
	else
	{
		RETURN_FFMpeg(create_ffmpeg_props(argv, props2, 1));

		if (props1->ctx->ch_layout.nb_channels < 2)
		{
			fputs("Invalid channels count in file1", stderr);
			return ERROR_FORMAT_INVALID;
		}
		RETURN_FFMpeg(add_samples(props2, array2, 1));
	}

	props1->samp = props1->ctx->sample_rate;
	props2->samp = props2->ctx->sample_rate;

	return SUCCESS;
}

int print_res(int delta, int argc, data *props1, data *props2)
{
	if (props1->samp != props2->samp && argc == 3)
	{
		fputs("Error: sample_rate1 != sample_rate2\n", stderr);
		return ERROR_UNSUPPORTED;
	}
	if (props1->samp == 0)
	{
		fputs("Error: sample_rate = zero\n", stderr);
		return ERROR_FORMAT_INVALID;
	}
	printf("delta: %i samples\nsample rate: %i Hz\ndelta time: %i ms\n",
		   delta,
		   props1->samp,
		   (int)((double)(delta * 1000) / props1->samp));
	return SUCCESS;
}
