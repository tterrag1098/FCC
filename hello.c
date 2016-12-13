// Write 'Hello, world.' ten times.

int foo;


int adder(int num)
{
	return 1;
}

void foo1()
{
    return ;
}

int bar()
{

}

int foobar(int test, float test2)
{
	int q, x, y, z;
	x = 4;
	if (test == 0)
	{
		q = 4;
		x = x + q;
	}
	y = 3;
	z = x * y + test;
	return z;
}

void proc()
{
	foo = 1;
}

int main()
{
	int i, j, k;
	i = 0;
	//x = 1;
	int abc;
	abc = 10;
	// Compound expression
	while (i < 10)
	{
		int bar;
		i = i + 1;
		// Simple if, compound statement
		if (abc == 4)
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
	rval = adder(); // Error (not enough params)
	rval = adder(abc); // OK
	rval = foobar(abc); // Error (not enough params)
	rval = foobar(abc, i); // OK
	rval = foobar(f, abc); // Error (invalid param types)
    rval = proc(); // Error (cannot assign to procedure)

	write(rval);
}

