#include "MineSweeper.h"

#include <QApplication>

int main(int argc, char *argv[])
{
	QApplication app(argc, argv);

	Minesweeper w;
	w.show();

	return app.exec();
}
