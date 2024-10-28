#include <stdint.h>
#include <stdio.h>

#define MERSENNE_NUMBER INT32_MAX

int64_t factorial(int64_t num)
{
	int64_t res = 1;
	for (int64_t i = 1; i <= num; i++)
	{
		res *= i;
		res %= MERSENNE_NUMBER;
	}
	return res;
}

void print_sep(uint16_t width1, uint16_t width2)
{
	printf("+");
	for (uint16_t i = 0; i < width1; i++)
	{
		printf("-");
	}
	printf("+");
	for (uint16_t i = 0; i < width2; i++)
	{
		printf("-");
	}
	printf("+\n");
}

uint8_t get_size(int64_t number)
{
	if (number == 0)
	{
		return 1;
	}
	uint8_t size = 0;
	int64_t num = number;
	while (num > 0)
	{
		size++;
		num /= 10;
	}
	return size;
}

void print_whitespace(uint16_t num)
{
	for (uint16_t i = 0; i < num; i++)
	{
		printf(" ");
	}
}

void print_line(int16_t width, int64_t num, int16_t align)
{
	uint8_t size = get_size(num);
	printf("|");
	if (align == -1)
	{
		printf(" %lld", num);
		print_whitespace(width - size - 1);
	}
	else if (align == 0)
	{
		print_whitespace((width - size + 1) / 2);
		printf("%lld", num);
		print_whitespace((width - size) / 2);
	}
	else
	{
		print_whitespace(width - size - 1);
		printf("%lld ", num);
	}
}

int main()
{
	int64_t const MODULE = UINT16_MAX + 1;
	int64_t n_start, n_end;
	int16_t align;
	int16_t width1 = 2, width2 = 2;

	scanf("%lld %lld %hd", &n_start, &n_end, &align);

	// check error
	if (n_start < 0 || n_end < 0 || align < -1 || align > 1 || n_start > UINT16_MAX || n_end > UINT16_MAX)
	{
		fprintf(stderr, "Bounds error.\n");
		return 1;
	}

	int64_t max_number;
	if (n_start > n_end)
	{
		n_end += MODULE;
		max_number = MODULE;
	}
	else
	{
		max_number = n_end;
	}

	while (max_number > 0)
	{
		width1++;
		max_number /= 10;
	}
	if (width1 == 2)
	{
		width1++;
	}

	int64_t max_factorial = factorial(n_start % MODULE);
	int64_t temp = max_factorial;
	for (int64_t i = n_start + 1; i <= n_end; i++)
	{
		// faster
		if ((i % MODULE) > ((i - 1) % MODULE))
		{
			temp = (temp * (i % MODULE)) % MERSENNE_NUMBER;
		}
		else
		{
			temp = factorial(i % MODULE);
		}
		if (max_factorial < temp)
		{
			max_factorial = temp;
		}
	}
	while (max_factorial > 0)
	{
		width2++;
		max_factorial /= 10;
	}
	if (width2 == 3)
	{
		width2++;
	}

	print_sep(width1, width2);
	printf("|");
	if (align == -1)
	{
		printf(" n");
		print_whitespace(width1 - 2);
		printf("|");
		printf(" n!");
		print_whitespace(width2 - 3);
		printf("|\n");
	}
	else if (align == 0)
	{
		print_whitespace(width1 / 2);
		printf("n");
		print_whitespace((width1 - 1) / 2);
		printf("|");
		print_whitespace((width2 - 1) / 2);
		printf("n!");
		print_whitespace((width2 - 2) / 2);
		printf("|\n");
	}
	else
	{
		print_whitespace(width1 - 2);
		printf("n |");
		print_whitespace(width2 - 3);
		printf("n! |\n");
	}
	print_sep(width1, width2);
	temp = factorial((n_start - 1) % MODULE);
	for (int64_t i = n_start; i <= n_end; i++)
	{
		// faster
		if ((i % MODULE) > ((i - 1) % MODULE) && i != 0)
		{
			temp = (temp * (i % MODULE)) % MERSENNE_NUMBER;
		}
		else
		{
			temp = factorial(i % MODULE);
		}
		print_line(width1, i % MODULE, align);
		print_line(width2, temp, align);
		printf("|\n");
	}
	print_sep(width1, width2);

	return 0;
}
