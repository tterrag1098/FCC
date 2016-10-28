// Write 'Hello, world.' ten times.

//int main() 
{	
	int i = 0;
	int abc = 10;
	// Compound expression
	while (true)
	{
		i = i + 1;
		// Simple if, compound statement
		if (abc > 4)
		{
			abc = i * (abc - 1);
		}
		// Simple if, single statement
		if (abc >= 2) i = i - 1;
		
		// Sync test
		/*
		if ((gibberish*#*(758iusdhf()
		{
			abc = abc - 2;
		}
		*/
	}
}