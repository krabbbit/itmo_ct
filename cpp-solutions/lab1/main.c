#include "return_codes.h"

#include <inttypes.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#define struct_n struct number
#define STRUCT_INIT                                                                                                    \
	{                                                                                                                  \
		0, 0, 0, 0, 0, 0, 0                                                                                            \
	}

struct number
{
	int32_t number;
	uint64_t mant;
	int32_t exp;
	uint8_t sign;
	uint8_t shift_bytes;
	uint8_t byte_round;
	uint8_t state;	  // 0 - ok, 1 - zero, 2 - inf, 3 - nan
};

uint8_t COUNT_EXP = 8;
uint8_t COUNT_MANT = 23;
int16_t SHIFT;

bool is_mul(char *operation)
{
	// m и x - для самодельных тестов
	return strcmp(operation, "\'*\'") == 0 || strcmp(operation, "*") == 0 || strcmp(operation, "m") == 0 ||
		   strcmp(operation, "x") == 0;
}

uint8_t scan_number(struct_n *num, char *str)
{
	if (sscanf(str, "%x", &(num->number)) != 1)
	{
		fprintf(stderr, "error when converting numbers from hexadecimal number system.\n");
		return ERROR_UNKNOWN;
	}
	return SUCCESS;
}

bool is_zero(struct_n *num)
{
	if (num->exp == -SHIFT && num->mant == (1 << COUNT_MANT))
	{
		num->state = 1;
		return true;
	}
	return false;
}

bool is_infinity(struct_n *num)
{
	if (imaxabs(num->exp - 1) == SHIFT && num->mant == (1 << COUNT_MANT))
	{
		num->state = 2;
		return true;
	}
	return false;
}

bool is_NaN(struct_n *num)
{
	if (imaxabs(num->exp - 1) == SHIFT && num->mant != (1 << COUNT_MANT))
	{
		num->state = 3;
		return true;
	}
	return false;
}

void round(struct_n *res, int16_t round_num)
{
	res->mant -= (1 << COUNT_MANT);
	if (((round_num == 1 && res->byte_round == 1 && res->shift_bytes > 1) ||
		 round_num == 1 && res->byte_round == 1 && res->mant % 2 != 0) ||
		round_num == 2 && res->shift_bytes > 0 && !res->sign || round_num == 3 && res->shift_bytes > 0 && res->sign)
	{
		res->mant++;
	}
	res->mant <<= (COUNT_MANT / 4 + 1) * 4 - COUNT_MANT;
}

uint8_t print_zero(uint8_t sign)
{
	if (sign == 1)
		printf("-");
	printf("0x0.0");
	for (uint8_t i = 0; i < COUNT_MANT / 4; i++)
		printf("0");
	printf("p+0");
	return SUCCESS;
}

uint8_t print_inf(uint8_t sign)
{
	if (sign == 1)
		printf("-");
	printf("inf\n");
	return SUCCESS;
}

uint8_t print_nan()
{
	printf("nan");
	return SUCCESS;
}

uint8_t special_cases_with_zero(struct_n *num1, struct_n *num2, char *operation)
{
	if (num1->sign != num2->sign && is_zero(num1) && strcmp(operation, "/") == 0 && !is_NaN(num2) ||
		num1->sign == 1 && is_zero(num1) && strcmp(operation, "-") == 0 && num2->sign == 0 && is_zero(num2) ||
		num1->sign == 1 && is_zero(num1) && strcmp(operation, "+") == 0 && num2->sign == 1 && is_zero(num2) ||
		num1->sign == 0 && !is_zero(num1) && is_mul(operation) && num2->sign == 1 && is_zero(num2))
		return print_zero(1);
	else if (num1->sign == 1 && is_zero(num1) && strcmp(operation, "/") == 0 && num2->sign == 1 && !is_zero(num2) ||
			 num1->sign != num2->sign && is_zero(num1) && strcmp(operation, "+") == 0 && is_zero(num2) ||
			 num1->sign == 1 && is_zero(num1) && is_mul(operation) && num2->sign == 1 && is_zero(num2) ||
			 num1->sign == 1 && is_zero(num1) && strcmp(operation, "-") == 0 && num2->sign == 1 && is_zero(num2))
		return print_zero(0);
	return 1;
}

uint8_t special_cases_with_nan(struct_n *num1, struct_n *num2, char *operation)
{
	if (strcmp(operation, "/") == 0 && (is_zero(num1) && is_zero(num2) || is_infinity(num1) && is_infinity(num2)) ||
		is_mul(operation) && (is_zero(num1) && is_infinity(num2) || is_infinity(num1) && is_zero(num2)) ||
		is_infinity(num1) && is_infinity(num2) &&
			(strcmp(operation, "+") == 0 && num1->sign != num2->sign || (strcmp(operation, "-") == 0 && num1->sign == num2->sign)) ||
		is_NaN(num1) || is_NaN(num2))
		return print_nan();
	return 1;
}

uint8_t special_cases_with_inf(struct_n *num1, struct_n *num2, char *operation)
{
	if (strcmp(operation, "/") == 0 && is_zero(num2))
	{
		if (is_zero(num1))
			return print_nan();
		else if (num1->sign == 0)
			return print_inf(0);
		else
			return print_inf(1);
	}
	return 1;
}

uint8_t print_(struct_n num, int16_t round_num)
{
	is_zero(&num);
	is_infinity(&num);
	is_NaN(&num);
	switch (num.state)
	{
	case 1:
		return print_zero(num.sign);
	case 2:
		return print_inf(num.sign);
	case 3:
		return print_nan();
	}
	round(&num, round_num);
	if (num.sign == 1)
		printf("-");
	printf("0x1.");
	uint8_t count_zeroes = (COUNT_MANT + 2) / 4;
	uint64_t tmp_mant = num.mant;
	while (tmp_mant > 0)
	{
		tmp_mant >>= 4;
		count_zeroes--;
	}
	while (count_zeroes > 0 && count_zeroes < 6)
	{
		count_zeroes--;
		printf("0");
	}
	printf("%llxp", num.mant);
	if (num.exp >= 0)
		printf("+");
	printf("%d", num.exp);
	return SUCCESS;
}

void to_normalize(struct_n *num)
{
	// create(num);
	uint8_t len_16_mant = 0;
	uint64_t tmp_mant = num->mant;
	for (; tmp_mant; tmp_mant >>= 4)
		len_16_mant++;
	if ((num->exp != -SHIFT) || num->mant == 0)
	{
		num->mant += (1 << COUNT_MANT);
	}
	else
	{
		num->exp = len_16_mant - SHIFT + 1 - COUNT_MANT;
		num->mant <<= COUNT_MANT - len_16_mant;
	}
}

void to_exp_format(struct_n *num)
{
	num->sign = (num->number >> (COUNT_MANT + COUNT_EXP)) & 1;
	num->exp = ((num->number >> COUNT_MANT) & ((1 << COUNT_EXP) - 1)) - (1 << (COUNT_EXP - 1)) + 1;
	num->mant = num->number & ((1 << COUNT_MANT) - 1);
	to_normalize(num);
}
void check_overflow(struct_n *num)
{
	num->exp += (num->mant >= (1 << (COUNT_MANT + 1)));
	while (num->mant >= (1 << (COUNT_MANT + 1)))
	{
		num->byte_round = 0;
		if (num->mant == ((num->mant >> 1) << 1) + 1)
		{
			num->shift_bytes++;
			num->byte_round = 1;
		}
		num->mant >>= 1;
	}
}

struct_n sum_sub(struct_n *num1, struct_n *num2, char *operation)
{
	struct_n result = STRUCT_INIT;
	if (num1->exp > num2->exp)
	{
		num2->mant >>= (num1->exp - num2->exp);
		result.exp = num1->exp;
	}
	else
	{
		num1->mant >>= (num2->exp - num1->exp);
		result.exp = num2->exp;
	}

	if (strcmp(operation, "+") == 0 && num1->sign == num2->sign || strcmp(operation, "-") == 0 && num1->sign != num2->sign)
	{
		result.mant = num1->mant + num2->mant;
		result.sign = num1->sign;
	}
	else if (num1->mant >= num2->mant)
	{
		result.mant = num1->mant - num2->mant;
		result.sign = num1->sign;
	}
	else
	{
		result.mant = num2->mant - num1->mant;
		result.sign = !num1->sign;
	}
	check_overflow(&result);
	return result;
}

struct_n mul_div(struct_n *num1, struct_n *num2, char *operation)
{
	struct_n result = STRUCT_INIT;
	if (num1->sign == num2->sign)
		result.sign = 0;
	else
		result.sign = 1;

	if (strcmp(operation, "/") == 0)
	{
		result.exp = num1->exp - num2->exp;
		result.mant = num1->mant / num2->mant;
	}
	else
	{
		result.exp = num1->exp + num2->exp;
		result.mant = num1->mant * num2->mant;
	}
	check_overflow(&result);
	if (strcmp(operation, "/") == 0)
		to_normalize(&result);
	return result;
}

int main(int argc, char *argv[])
{
	if (argc != 4 && argc != 6)
	{
		fprintf(stderr, "The command line contains the wrong number of arguments.\n");
		return ERROR_ARGUMENTS_INVALID;
	}
	char *format = argv[1];

	int16_t type;
	uint8_t code = sscanf(argv[2], "%hd", &type);
	if (code != 1 || strcmp(format, "f") != 0 && strcmp(format, "h") != 0 || type > 3 || type < 0)
	{
		fprintf(stderr, "Error when converting rounding or number format.\n");
		return ERROR_ARGUMENTS_INVALID;
	}
	if (strcmp(format, "f") == 0)
	{
		COUNT_EXP = 8;
		COUNT_MANT = 23;
	}
	else
	{
		COUNT_EXP = 5;
		COUNT_MANT = 10;
	}
	SHIFT = (1 << (COUNT_EXP - 1)) - 1;
	char *operation;
	struct_n num1 = STRUCT_INIT, num2 = STRUCT_INIT;
	uint8_t res = scan_number(&num1, argv[3]);
	if (res != 0)
	{
		fprintf(stderr, "Error when scan number\n");
		return ERROR_DATA_INVALID;
	}
	to_exp_format(&num1);
	if (argc == 6)
	{
		operation = argv[4];
		res = scan_number(&num2, argv[5]);
		if (res != 0)
		{
			fprintf(stderr, "Error when scan number\n");
			return ERROR_DATA_INVALID;
		}
		to_exp_format(&num2);
		if (special_cases_with_nan(&num1, &num2, operation) == SUCCESS || special_cases_with_inf(&num1, &num2, operation) == SUCCESS ||
			special_cases_with_zero(&num1, &num2, operation) == SUCCESS)
			return SUCCESS;
		if (strcmp(operation, "+") == 0 || strcmp(operation, "-") == 0)
		{
			print_(sum_sub(&num1, &num2, operation), type);
		}
		else if (is_mul(operation) || strcmp(operation, "/") == 0)
		{
			print_(mul_div(&num1, &num2, operation), type);
		}
		else
		{
			fprintf(stderr, "Uncorrected operation sign\n");
			return ERROR_ARGUMENTS_INVALID;
		}
		return SUCCESS;
	}
	print_(num1, type);
	return SUCCESS;
}
