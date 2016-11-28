// Write 'Hello, world.' ten times.

int foo;


int adder(int num)
{
	return 1;
}

int foobar(int test, float test2)
{
	int x, y;
	x = 4;
	y = 3;
	int z;
	z = x * y + test;
	return z;
}

int main()
{
	int i, j, k;
	i = 0;
	//x = 1;
	int abc;
	abc = 10;
	// Compound expression
	while (true)
	{
		int bar;
		i = i + 1;
		// Simple if, compound statement
		if (abc > 4)
		{
			int baz;
			abc = i * (abc - 1);
		}
		// Simple if, single statement
		if (abc >= 2) { int q; q = i - 1; }

		// Sync test
		/*
		if ((gibberish*#*(758iusdhf()
		{
			abc = abc - 2;
		}
		*/
	}

	float f;
	f = 0.3;
	
	int rval;
	rval = adder(); // Error
	rval = adder(abc); // OK
	rval = foobar(abc); // Error
	rval = foobar(abc, i); // OK
	rval = foobar(f, abc); // Error

	return i;
}

