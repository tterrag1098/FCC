/*
 * This is newton2.pas, translated into SubsetC.
 *
 * It functions identically, except for the text printouts,
 * since we don't support string literals.
 */

float epsilon;
int number;
int foo;
int abc;


int test1(int j, int)  // missing second identifier
{
    return 1;
}


void test2()  // cannot return in a void
{
    return 1;
}


void proc()
{
	foo = 1;
}

int foobar(int a)
{
    return a;
}

int adder(int a, int b)
{
    return a+b;
}

float root(float x)
{
    abc = 10;
    float r;
    int rr;
    r = 1 // missing semi-colon
    while (abs(x / sqr(r) - 1) >=)  // missing end of conditional
    {
        rr = (x / r + r) / 2;  // incompatible types
    }
    return;  // missing token to return
}

void print(int n, float root)
{
    // We don't support strings, so just print one after the other
    writeln(n);
    writeln(root);
}

int main()
{
    int i;
    float f;
    epsilon = 0.000006;
    number = -1;
    while (number != 0)
    {
        writeln();
        read(number);

        if (number == 0)
        {
            print(number, 0.0);
        }
		if (number > 0)
        {
                print(number, root(number));
        }
    }

	int rval;
	rval = foobar(); // Error (not enough params)
	rval = foobar(abc); // OK
	rval = adder(abc); // Error (not enough params)
	rval = adder(abc, i); // OK
	rval = adder(f, abc); // Error (invalid param types)
    rval = proc(); // Error (cannot assign to procedure)

    writeln(rval);
}
