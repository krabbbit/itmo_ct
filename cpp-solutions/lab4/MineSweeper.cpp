#include "Minesweeper.h"

#include <QComboBox>
#include <QCommandLineParser>
#include <QDebug>
#include <QDialog>
#include <QFile>
#include <QFormLayout>
#include <QGridLayout>
#include <QMessageBox>
#include <QMouseEvent>
#include <QRandomGenerator>
#include <QSettings>
#include <QSpinBox>
#include <QStatusBar>
#include <QTextStream>
#include <QToolBar>
#include <QVector>

Minesweeper::Minesweeper(QWidget *parent) : QMainWindow(parent)
{
	args = QCoreApplication::arguments();
	centralWidget = new QWidget(this);
	setCentralWidget(centralWidget);

	gridLayout = new QGridLayout(centralWidget);
	gridLayout->setSpacing(0);
	languageComboBox = new QComboBox();
	languageComboBox->addItem("Русский", "ru_RU");
	languageComboBox->addItem("Татарча", "tt_RU");
	createToolBar_();
	createStartWindow_();
}

void Minesweeper::createToolBar_()
{
	// gridLayout = new QGridLayout(centralWidget);
	// gridLayout->setSpacing(0);
	toolBar = addToolBar("Main Toolbar");

	QAction *restart = new QAction(QIcon(":/images/restart1.png"), "", this);
	QAction *restartInit = new QAction(QIcon(":/images/restart2.png"), "", this);
	QAction *levsha = new QAction(QIcon(":/images/levsha.png"), "", this);
	QAction *dbg = new QAction(QIcon(":/images/dbg.png"), "", this);

	connect(
		restart,
		&QAction::triggered,
		this,
		[&]()
		{
			ifThisParams_ = true;
			createStartWindow_();
		});
	connect(
		restartInit,
		&QAction::triggered,
		this,
		[&]()
		{
			ifThisParams_ = false;
			createStartWindow_();
		});
	connect(levsha, &QAction::triggered, this, [&]() { isLevsha_ = !isLevsha_; });
	connect(
		dbg,
		&QAction::triggered,
		this,
		[&]()
		{
			isDbg_ = !isDbg_;
			for (int i = 0; i < rows_; i++)
			{
				for (int j = 0; j < cols_; j++)
				{
					if (isDbg_ && grid[i][j] == -1)
					{
						buttons[i][j]->setStyleSheet("background-color:red");
					}
					else if (i != rLast_ || j != cLast_)
					{
						buttons[i][j]->setStyleSheet("");
					}
				}
			}
		});
	toolBar->addAction(restart);
	toolBar->addAction(restartInit);
	toolBar->addAction(levsha);
	toolBar->actions()[0]->setToolTip(tr("Рестарт"));
	toolBar->actions()[1]->setToolTip(tr("Рестарт с новыми параметрами игры"));
	toolBar->actions()[2]->setToolTip(tr("ЛКМ теперь ПКМ и наоборот"));
	if (args.contains("dbg"))
	{
		toolBar->addAction(dbg);
		toolBar->actions()[3]->setToolTip(tr("Подглядеть за минами"));
	}

	foreach (QAction *action, toolBar->actions())
	{
		toolBar->widgetForAction(action)->setFixedSize(35, 35);
	}

	toolBar->addWidget(languageComboBox);
	lCode_ = 0;
	connect(languageComboBox, QOverload< int >::of(&QComboBox::currentIndexChanged), this, &Minesweeper::switchLanguage_);
	switchLanguage_(lCode_);

	QWidget *spacer = new QWidget(this);
	spacer->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Preferred);
	toolBar->addWidget(spacer);
	countMinesLabel = new QLabel(this);

	QFont font = countMinesLabel->font();
	font.setPointSize(16);
	font.setBold(true);
	countMinesLabel->setFont(font);

	countMinesLabel->setStyleSheet("QLabel { color : lime; }");

	countMinesLabel->setAlignment(Qt::AlignRight | Qt::AlignVCenter);
	toolBar->addWidget(countMinesLabel);
}

void Minesweeper::switchLanguage_(int index)
{
	lCode_ = index;
	QComboBox *combo = qobject_cast< QComboBox * >(sender());
	if (combo)
	{
		QString langCode = combo->itemData(index).toString();

		qApp->removeTranslator(&trans);

		if (trans.load(":MineSweeper_" + langCode + ".qm"))
		{
			qApp->installTranslator(&trans);
		}

		updateUI_();
	}
}

void Minesweeper::updateUI_()
{
	toolBar->actions()[0]->setToolTip(tr("Рестарт"));
	toolBar->actions()[1]->setToolTip(tr("Рестарт с новыми параметрами игры"));
	toolBar->actions()[2]->setToolTip(tr("ЛКМ теперь ПКМ и наоборот"));
	if (args.contains("dbg"))
	{
		toolBar->actions()[3]->setToolTip(tr("Подглядеть за минами"));
	}
	dialogParams->setWindowTitle(tr("Выбор параметров игры"));
	rowsLabel->setText(tr("Строки:"));
	colsLabel->setText(tr("Столбцы:"));
	minesLabel->setText(tr("Мины:"));
	playButton->setText(tr("Играть!"));
	defaultButton->setText(tr("Запустить сохранение"));
}

void Minesweeper::initVars_()
{
	centralWidget->setEnabled(true);
	buttons = QVector< QVector< QPushButton * > >(rows_, QVector< QPushButton * >(cols_));
	grid = QVector< QVector< int > >(rows_, QVector< int >(cols_, 0));
	revealed = QVector< QVector< bool > >(rows_, QVector< bool >(cols_, false));
	flagged = QVector< QVector< bool > >(rows_, QVector< bool >(cols_, false));
	questions = QVector< QVector< bool > >(rows_, QVector< bool >(cols_, false));
	countUnpressedButtons_ = rows_ * cols_ - mines_;
	countMinesForLabel_ = mines_;
	countMinesLabel->setText(QString::number(countMinesForLabel_));
	isLevsha_ = false;
	isDbg_ = false;
	isLose_ = false;
	isStartMines_ = true;
	rLast_ = -1;
	cLast_ = -1;
}
void Minesweeper::createStartWindow_()
{
	if (!ifThisParams_)
	{
		dialogParams = new QDialog(this);
		dialogParams->setWindowTitle(tr("Выбор параметров игры"));
		dialogParams->setBaseSize(300, 200);
		QVBoxLayout *mainLayout = new QVBoxLayout(dialogParams);

		formLayout = new QFormLayout;

		QSpinBox *rows_Input = new QSpinBox(dialogParams);
		rows_Input->setFixedSize(80, 35);
		rows_Input->setRange(4, 20);
		rows_Input->setValue(rows_);

		QSpinBox *cols_Input = new QSpinBox(dialogParams);
		cols_Input->setFixedSize(80, 35);
		cols_Input->setRange(4, 20);
		cols_Input->setValue(cols_);

		QSpinBox *mines_Input = new QSpinBox(dialogParams);
		mines_Input->setFixedSize(80, 35);
		mines_Input->setRange(1, 399);
		mines_Input->setValue(mines_);

		rowsLabel = new QLabel(tr("Строки:"));
		colsLabel = new QLabel(tr("Столбцы:"));
		minesLabel = new QLabel(tr("Мины:"));

		formLayout->addRow(rowsLabel, rows_Input);
		formLayout->addRow(colsLabel, cols_Input);
		formLayout->addRow(minesLabel, mines_Input);

		mainLayout->addLayout(formLayout);
		QHBoxLayout *buttonLayout = new QHBoxLayout;
		buttonLayout->addStretch(1);

		playButton = new QPushButton(tr("Играть!"), dialogParams);
		defaultButton = new QPushButton(tr("Запустить сохранение"), dialogParams);

		buttonLayout->addWidget(playButton);
		buttonLayout->addWidget(defaultButton);

		mainLayout->addLayout(buttonLayout);
		QObject::connect(
			playButton,
			&QPushButton::clicked,
			[&]()
			{
				if (rows_Input->value() * cols_Input->value() <= mines_Input->value())
				{
					QMessageBox::information(this, tr("Ошибка в данных"), tr("Количество мин слишком большое!"));
				}
				else
				{
					rows_ = rows_Input->value();
					cols_ = cols_Input->value();
					mines_ = mines_Input->value();
					initVars_();
					dialogParams->accept();
				}
			});

		QObject::connect(
			defaultButton,
			&QPushButton::clicked,
			[&]()
			{
				isSave_ = true;
				loadFile_(file_name);
				dialogParams->reject();
			});
		dialogParams->exec();
	}
	else
	{
		initVars_();
	}

	createGrid_();
	initializeGrid_();
}

void Minesweeper::clearGrid_()
{
	if (gridLayout != nullptr)
	{
		QLayoutItem *item;
		while ((item = gridLayout->takeAt(0)) != nullptr)
		{
			if (item->widget())
			{
				delete item->widget();
			}
			delete item;
		}
		delete gridLayout;
	}

	gridLayout = new QGridLayout(centralWidget);
}

void Minesweeper::createGrid_()
{
	clearGrid_();
	gridLayout->setContentsMargins(0, 0, 0, 0);
	gridLayout->setSpacing(0);
	centralWidget->setContentsMargins(0, 0, 0, 0);
	this->adjustSize();
	for (int i = 0; i < rows_; ++i)
	{
		for (int j = 0; j < cols_; ++j)
		{
			QPushButton *button = new QPushButton("", this);
			button->setFixedSize(30, 30);
			gridLayout->addWidget(button, i, j);
			button->installEventFilter(this);
			buttons[i][j] = button;
			buttons[i][j]->setEnabled(true);
		}
	}
}

void Minesweeper::initializeGrid_()
{
	if (!isSave_)
	{
		std::srand(std::time(nullptr));
		for (int i = 0; i < rows_; ++i)
		{
			for (int j = 0; j < cols_; ++j)
			{
				grid[i][j] = 0;
				revealed[i][j] = false;
				flagged[i][j] = false;
				questions[i][j] = false;
				buttons[i][j]->setText("");
				buttons[i][j]->setEnabled(true);
			}
		}
	}
	else
	{
		paint_();
		isSave_ = false;
	}
	countMinesLabel->setText(QString::number(countMinesForLabel_));
}

void Minesweeper::createMinesPos_(int r, int c)
{
	for (int i = 0; i < mines_;)
	{
		int row = QRandomGenerator::global()->bounded(rows_);
		int col = QRandomGenerator::global()->bounded(cols_);
		if ((row != r || col != c) && grid[row][col] != -1)
		{
			grid[row][col] = -1;
			++i;
			for (int di = -1; di <= 1; ++di)
			{
				for (int dj = -1; dj <= 1; ++dj)
				{
					int ni = row + di, nj = col + dj;
					if (ni >= 0 && ni < rows_ && nj >= 0 && nj < cols_ && grid[ni][nj] != -1)
					{
						++grid[ni][nj];
					}
				}
			}
		}
		else
		{
			continue;
		}
	}
}

void Minesweeper::paint_()
{
	for (int i = 0; i < rows_; i++)
	{
		for (int j = 0; j < cols_; j++)
		{
			if (revealed[i][j])
			{
				buttons[i][j]->setText(QString::number(grid[i][j]));
			}
			if (flagged[i][j])
			{
				buttons[i][j]->setText("!");
			}
			if (questions[i][j])
			{
				buttons[i][j]->setText("?");
			}
		}
	}
}

void Minesweeper::setFlagged_(int r, int c)
{
	if (!flagged[r][c] && !questions[r][c])
	{
		buttons[r][c]->setText("!");
		flagged[r][c] = true;
		countMinesLabel->setText(QString::number(countMinesLabel->text().toInt() - 1));
	}
	else if (flagged[r][c] && !questions[r][c])
	{
		buttons[r][c]->setText("?");
		questions[r][c] = true;
		countMinesLabel->setText(QString::number(countMinesLabel->text().toInt() + 1));
	}
	else
	{
		flagged[r][c] = false;
		questions[r][c] = false;
		revealed[r][c] = false;
		buttons[r][c]->setText("");
	}
}

bool Minesweeper::eventFilter(QObject *obj, QEvent *event)
{
	QPushButton *button = qobject_cast< QPushButton * >(obj);
	if (!button)
	{
		return QMainWindow::eventFilter(obj, event);
	}

	if (event->type() == QEvent::MouseButtonPress)
	{
		QMouseEvent *mouseEvent = static_cast< QMouseEvent * >(event);

		int row = -1, col = -1;
		for (int i = 0; i < rows_; ++i)
		{
			for (int j = 0; j < cols_; ++j)
			{
				if (buttons[i][j] == button)
				{
					row = i;
					col = j;
					break;
				}
			}
			if (row != -1)
				break;
		}

		if (mouseEvent->button() == Qt::LeftButton)
		{
			if (isLevsha_ && !revealed[row][col])
			{
				setFlagged_(row, col);
			}
			else if (!flagged[row][col] && !questions[row][col])
			{
				onButtonClicked_(row, col);
			}
			return true;
		}
		else if (mouseEvent->button() == Qt::RightButton)
		{
			if (isLevsha_ && !flagged[row][col] && !questions[row][col])
			{
				onButtonClicked_(row, col);
			}
			else if (!revealed[row][col])
			{
				setFlagged_(row, col);
			}
			return true;
		}
		else if (mouseEvent->button() == Qt::MiddleButton && revealed[row][col])
		{
			int isNowLose = false;
			;
			for (int di = -1; di <= 1; ++di)
			{
				for (int dj = -1; dj <= 1; ++dj)
				{
					int ni = row + di, nj = col + dj;
					if (ni >= 0 && ni < rows_ && nj >= 0 && nj < cols_)
					{
						if ((grid[ni][nj] == -1 && !flagged[ni][nj]) || (grid[ni][nj] != -1 && flagged[ni][nj]))
						{
							windowLose_(row, col);
							return true;
						}
					}
				}
			}
			for (int di = -1; di <= 1; ++di)
			{
				for (int dj = -1; dj <= 1; ++dj)
				{
					int ni = row + di, nj = col + dj;
					if (ni >= 0 && ni < rows_ && nj >= 0 && nj < cols_)
					{
						if (grid[ni][nj] != -1)
						{
							onButtonClicked_(ni, nj);
						}
					}
				}
			}
		}
	}

	return QMainWindow::eventFilter(obj, event);
}

void Minesweeper::onButtonClicked_(int row, int col)
{
	if (isStartMines_)
	{
		createMinesPos_(row, col);
		isStartMines_ = false;
	}
	if (revealed[row][col])
		return;

	revealed[row][col] = true;
	if (grid[row][col] == -1)
	{
		windowLose_(row, col);
	}
	else if (countUnpressedButtons_ <= 1)
	{
		windowGameOver_(tr("Поздавляем!"), tr("Ты прошел игру!"));
	}
	else if (!flagged[row][col] && !questions[row][col])
	{
		buttons[row][col]->setText(QString::number(grid[row][col]));
		buttons[row][col]->setEnabled(false);
		countUnpressedButtons_--;
		if (grid[row][col] == 0)
		{
			for (int di = -1; di <= 1; ++di)
			{
				for (int dj = -1; dj <= 1; ++dj)
				{
					int ni = row + di, nj = col + dj;
					if (ni >= 0 && ni < rows_ && nj >= 0 && nj < cols_ && !revealed[ni][nj])
					{
						onButtonClicked_(ni, nj);
					}
				}
			}
		}
	}
}

void Minesweeper::windowGameOver_(QString title, QString message)
{
	titleGameOver = title;
	textGameOver = message;
	fieldOpen_();
	centralWidget->setEnabled(false);
	for (int i = 0; i < rows_; i++)
	{
		for (int j = 0; j < cols_; j++)
		{
			buttons[i][j]->setEnabled(false);
			revealed[i][j] = true;
		}
	}
	dialogGameOver = new QDialog(this);
	dialogGameOver->setWindowTitle(title);
	dialogGameOver->setBaseSize(300, 200);
	QVBoxLayout *mainLayout = new QVBoxLayout(dialogGameOver);
	textLabelGameOver = new QLabel();
	textLabelGameOver->setText(message);
	mainLayout->addWidget(textLabelGameOver);
	QHBoxLayout *buttonLayout = new QHBoxLayout(dialogGameOver);
	buttonLayout->addStretch(1);

	restartButton = new QPushButton(tr("Еще раз!"), dialogGameOver);
	paramsButton = new QPushButton(tr("Выбрать параметры игры"), dialogGameOver);

	buttonLayout->addWidget(restartButton);
	buttonLayout->addWidget(paramsButton);
	mainLayout->addLayout(buttonLayout);
	QObject::connect(
		restartButton,
		&QPushButton::clicked,
		[&]()
		{
			ifThisParams_ = true;
			createStartWindow_();
			dialogGameOver->accept();
		});

	QObject::connect(
		paramsButton,
		&QPushButton::clicked,
		[&]()
		{
			ifThisParams_ = false;
			createStartWindow_();
			dialogGameOver->reject();
		});
	dialogGameOver->exec();
}

void Minesweeper::fieldOpen_()
{
	for (int i = 0; i < rows_; i++)
	{
		for (int j = 0; j < cols_; j++)
		{
			if (grid[i][j] == -1)
			{
				buttons[i][j]->setText("*");
			}
			else
			{
				buttons[i][j]->setText(QString::number(grid[i][j]));
			}
		}
	}
}

void Minesweeper::windowLose_(int row, int col)
{
	isLose_ = true;
	fieldOpen_();
	buttons[row][col]->setStyleSheet("background-color:red");
	rLast_ = row;
	cLast_ = col;
	windowGameOver_(
		tr("Игра окончена..."),
		tr("Ты подорвался на мине!\nНелюбимая поговорка саперов: Одна "
		   "нога здесь, другая - там."));
}

void Minesweeper::saveFile_(const QString &fileName)
{
	QFile file(fileName);
	file.setPermissions(QFileDevice::ReadOwner | QFileDevice::WriteOwner);
	if (!file.open(QIODevice::WriteOnly))
	{
		return;
	}

	QTextStream out(&file);

	out << rows_ << " " << cols_ << " " << mines_ << " " << countMinesLabel->text() << " " << lCode_ << "\n";

	for (int r = 0; r < rows_; ++r)
	{
		for (int c = 0; c < cols_; ++c)
		{
			out << r << " " << c << " " << grid[r][c] << " " << revealed[r][c] << " " << flagged[r][c] << " "
				<< questions[r][c] << "\n";
		}
	}

	file.close();
}

void Minesweeper::loadFile_(const QString &fileName)
{
	QFile file(fileName);
	file.setPermissions(QFileDevice::ReadOwner | QFileDevice::WriteOwner);
	if (!file.open(QIODevice::ReadOnly))
	{
		return;
	}
	QTextStream in(&file);

	int lRows, lCols, lMines, lText, lLang;
	in >> lRows >> lCols >> lMines >> lText >> lLang;
	if (lRows > 0)
	{
		rows_ = lRows;
		cols_ = lCols;
		mines_ = lMines;
		initVars_();
		countMinesForLabel_ = lText;
		lCode_ = lLang;
		QString langCode = languageComboBox->itemData(lCode_).toString();
		qApp->removeTranslator(&trans);
		if (trans.load(":Minesweeper_" + langCode + ".qm"))
		{
			qApp->installTranslator(&trans);
		}
		updateUI_();
		languageComboBox->setCurrentIndex(lLang);
		for (int r = 0; r < rows_; r++)
		{
			for (int c = 0; c < cols_; c++)
			{
				int row_, col_, isMine_, isRevealed_, isFlagged_, isQuestions_;
				in >> row_ >> col_ >> isMine_ >> isRevealed_ >> isFlagged_ >> isQuestions_;
				grid[row_][col_] = isMine_;
				revealed[row_][col_] = isRevealed_;
				flagged[row_][col_] = isFlagged_;
				questions[row_][col_] = isQuestions_;
			}
		}
	}
	else
	{
		lCode_ = 0;
		initVars_();
	}
	file.close();
}

void Minesweeper::closeEvent(QCloseEvent *event)
{
	if (!isLose_)
		saveFile_(file_name);
}
