#ifndef QUAT_HPP
#define QUAT_HPP
#include <array>
#include <cmath>
#include <stdexcept>

template< typename T >
struct matrix_t
{
	T data[16];
};

template< typename T >
struct vector3_t
{
	T x, y, z;
};

template< typename T >
class Quat
{
  public:
	explicit Quat(T a = 0, T b = 0, T c = 0, T d = 0) : m_value{ b, c, d, a } {}

	Quat(T angle, bool isRadian, vector3_t< T > vector)
	{
		if (!isRadian)
		{
			angle *= T(M_PI) / 180;
		}
		T half = angle / T(2);
		T norma = T(std::sqrt(sqr(vector.x) + sqr(vector.y) + sqr(vector.z)));
		isDivideByZero(norma);
		T vect_array[3] = { vector.x, vector.y, vector.z };
		for (size_t i = 0; i < std::size(vect_array); i++)
		{
			m_value[i] = vect_array[i] * sin(half) / norma;
		}
		m_value[3] = std::cos(half);
	}

	Quat operator+(const Quat& quat) const
	{
		return Quat(m_value[3] + quat.m_value[3], m_value[0] + quat.m_value[0], m_value[1] + quat.m_value[1], m_value[2] + quat.m_value[2]);
	}

	Quat operator-() const { return Quat(-m_value[3], -m_value[0], -m_value[1], -m_value[2]); }

	Quat operator-(const Quat< T >& quat) const { return *this + (-quat); }

	Quat& operator+=(const Quat< T >& quat)
	{
		*this = *this + quat;
		return *this;
	}

	Quat& operator-=(const Quat< T >& quat) { return *this += (-quat); }

	Quat operator*(const Quat< T >& quat) const
	{
		return Quat(
			m_value[3] * quat.m_value[3] - m_value[0] * quat.m_value[0] - m_value[1] * quat.m_value[1] - m_value[2] * quat.m_value[2],
			m_value[3] * quat.m_value[0] + m_value[0] * quat.m_value[3] + m_value[1] * quat.m_value[2] - m_value[2] * quat.m_value[1],
			m_value[3] * quat.m_value[1] + m_value[1] * quat.m_value[3] + m_value[2] * quat.m_value[0] - m_value[0] * quat.m_value[2],
			m_value[3] * quat.m_value[2] + m_value[0] * quat.m_value[1] + m_value[2] * quat.m_value[3] - m_value[1] * quat.m_value[0]);
	}

	Quat operator~() const { return Quat(m_value[3], -m_value[0], -m_value[1], -m_value[2]); }

	bool operator==(const Quat& quat) const
	{
		for (size_t i = 0; i < std::size(m_value); i++)
		{
			if (m_value[i] != quat.m_value[i])
				return false;
		}
		return true;
	}

	bool operator!=(const Quat& quat) const { return !(*this == quat); }

	explicit operator T() const
	{
		return std::sqrt(sqr(m_value[0]) + sqr(m_value[1]) + sqr(m_value[2]) + sqr(m_value[3]));
	}

	Quat operator*(const T num) const
	{
		return Quat(m_value[3] * num, m_value[0] * num, m_value[1] * num, m_value[2] * num);
	}

	Quat operator/(const T num) const
	{
		isDivideByZero(num);
		return Quat(m_value[3] / num, m_value[0] / num, m_value[1] / num, m_value[2] / num);
	}

	Quat operator*(const vector3_t< T >& vector) const { return *this * Quat(0, vector.x, vector.y, vector.z); }

	matrix_t< T > matrix() const
	{
		return matrix_t< T >{
			m_value[3], -m_value[0], -m_value[1], -m_value[2], m_value[0], m_value[3],	-m_value[2], m_value[1],
			m_value[1], m_value[2],	 m_value[3],  -m_value[0], m_value[2], -m_value[1], m_value[0],	 m_value[3]
		};
	}

	T angle(bool isRadian = true) const
	{
		T result_angle = T(2) * std::acos(m_value[3]);
		if (!isRadian)
			return result_angle * T(180) / M_PI;
		return result_angle;
	}

	matrix_t< T > rotation_matrix() const
	{
		vector3_t< T > vect1 = apply({ 1, 0, 0 });
		vector3_t< T > vect2 = apply({ 0, 1, 0 });
		vector3_t< T > vect3 = apply({ 0, 0, 1 });
		matrix_t< T > result_matrix = {
			vect1.x, vect1.y, vect1.z, 0, vect2.x, vect2.y, vect2.z, 0, vect3.x, vect3.y, vect3.z, 0, 0, 0, 0, 1
		};
		return result_matrix;
	}

	vector3_t< T > apply(const vector3_t< T >& vector) const
	{
		T norma = static_cast< T >(*this);
		Quat< T > result_normal = *this / norma;
		Quat< T > result_normal_shift(0, vector.x, vector.y, vector.z);
		Quat< T > apply_res = result_normal * result_normal_shift * ~(result_normal);
		return { apply_res.m_value[0], apply_res.m_value[1], apply_res.m_value[2] };
	}

	const T* data() const { return m_value; };

  private:
	T m_value[4];
	T sqr(const T value) const { return value * value; }
	void isDivideByZero(const T value) const
	{
		if (value == T(0))
			throw std::logic_error("Error: division by zero!");
	}
};
#endif /* QUAT_HPP */
