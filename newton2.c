/*
 * This is newton2.pas, translated into SubsetC.
 *
 * It functions identically, except for the text printouts,
 * since we don't support string literals.
 */

float epsilon;
int number;

float root(float x)
{
    float r;
    r = 1;
    while (abs(x / sqr(r) - 1) >= epsilon)
    {
        r = (x / r + r) / 2;
    }
    return r;
}

void print(int n, float root)
{
    // We don't support strings, so just print one after the other
    writeln(n);
    writeln(root);
}

int main()
{
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
        // No else statements either :(
		if (number > 0)
        {
                print(number, root(number));
        }
    }
}
