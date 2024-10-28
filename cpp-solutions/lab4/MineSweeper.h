#ifndef MINESWEEPER_H
#define MINESWEEPER_H

#include <QComboBox>
#include <QDialog>
#include <QFormLayout>
#include <QGridLayout>
#include <QLabel>
#include <QMainWindow>
#include <QPushButton>
#include <QSpinBox>
#include <QTranslator>
#include <QVector>

class Minesweeper : public QMainWindow
{
	Q_OBJECT

  public:
	explicit Minesweeper(QWidget *parent = nullptr);

  protected:
	bool eventFilter(QObject *obj, QEvent *event) override;
	void closeEvent(QCloseEvent *event) override;

  private slots:
	void onButtonClicked_(int row, int col);
	void windowGameOver_(QString title, QString message);
	void switchLanguage_(int index);

  private:
	QVector< QVector< QPushButton * > > buttons;
	QVector< QVector< int > > grid;
	QVector< QVector< bool > > revealed;
	QVector< QVector< bool > > flagged;
	QVector< QVector< bool > > questions;
	QLabel *countMinesLabel;
	QGridLayout *gridLayout;
	QWidget *menuWidget;
	QGridLayout *menuLayout;
	QWidget *centralWidget;
	QToolBar *toolBar;
	int countUnpressedButtons_;
	int isLevsha_ = false;
	int isDbg_ = false;

	void initializeGrid_();
	void createGrid_();
	void createToolBar_();
	void paint_();
	void changeIconLevsha_();
	void windowLose_(int row, int col);
	void saveFile_(const QString &fileName);
	void loadFile_(const QString &fileName);
	void createTextForToolBar_();
	void clearGrid_();
	int thisRow_, thisCol_;
	bool isSave_ = false;
	bool isLose_ = false;
	int countMinesForLabel_;
	int rLast_;
	int cLast_;
	void createMinesPos_(int r, int c);
	void fieldOpen_();
	bool isStartMines_ = true;
	QStringList args;
	QString file_name = "config_sweeper.ini";
	QTranslator trans;
	void updateUI_();
	QComboBox *languageComboBox;
	QDialog *dialogParams;
	QFormLayout *formLayout;
	QLabel *rowsLabel;
	QLabel *colsLabel;
	QLabel *minesLabel;
	QPushButton *playButton;
	QPushButton *defaultButton;
	QDialog *dialogGameOver;
	QString titleGameOver;
	QString textGameOver;
	QLabel *textLabelGameOver;
	QPushButton *restartButton;
	QPushButton *paramsButton;
	int lCode_;
	void setFlagged_(int r, int c);
	void initVars_();
	void createStartWindow_();
	bool ifThisParams_ = false;
	int rows_ = 10;
	int cols_ = 10;
	int mines_ = 10;
};

#endif	  // MINESWEEPER_H
