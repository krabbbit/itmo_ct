#ifndef FUNC_RETURN_CODE_H
#define FUNC_RETURN_CODE_H

#define RETURN_AND_FREE_FFMpeg(code)                                                                                   \
	do                                                                                                                 \
	{                                                                                                                  \
		if ((code) != SUCCESS)                                                                                         \
		{                                                                                                              \
			goto lCleanup;                                                                                             \
		}                                                                                                              \
	} while (0)

#define RETURN_FFMpeg(code)                                                                                            \
	do                                                                                                                 \
	{                                                                                                                  \
		if ((code) != SUCCESS)                                                                                         \
		{                                                                                                              \
			return (code);                                                                                             \
		}                                                                                                              \
	} while (0)

#endif
