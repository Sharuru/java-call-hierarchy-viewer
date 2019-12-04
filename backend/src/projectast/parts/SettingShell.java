package projectast.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import projectast.views.PSTView.SharedData;
import org.eclipse.wb.swt.SWTResourceManager;

public class SettingShell extends Shell {

    private Text urlText;
    private Text usernameText;
    private Text passwordText;
    static SettingShell settingShell;
    private Text tableNameTxt;

    public static void main(SharedData sharedData, Display parentDisplay) {

        Display currentShellDisplay = Display.getDefault();
        SettingShell settingShell = new SettingShell(parentDisplay, sharedData);
        SettingShell.settingShell = settingShell;
        // make shell always popup in screen center
        Monitor primary = currentShellDisplay.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = settingShell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        settingShell.setLocation(x, y);

        // initialize
        settingShell.open();
        settingShell.layout();
        while (!settingShell.isDisposed()) {
            if (!currentShellDisplay.readAndDispatch()) {
                currentShellDisplay.sleep();
            }
        }
    }

    public SettingShell(Display display, SharedData sharedData) {

        super(display, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.TITLE | SWT.ON_TOP);
        setModified(true);

        Group jdbcSettingGroup = new Group(this, SWT.NONE);
        jdbcSettingGroup.setText("JDBC Settings");
        jdbcSettingGroup.setBounds(10, 10, 424, 128);

        Label urlLabel = new Label(jdbcSettingGroup, SWT.NONE);
        urlLabel.setBounds(69, 23, 21, 15);
        urlLabel.setText("URL");
        urlText = new Text(jdbcSettingGroup, SWT.BORDER | SWT.WRAP | SWT.MULTI);
        urlText.setText("jdbc:postgresql://database:5432/database");
        urlText.setBounds(96, 20, 318, 44);
        if (!sharedData.getJdbcUrl().isEmpty()) {
            urlText.setText(sharedData.getJdbcUrl());
        }

        Label usernameLabel = new Label(jdbcSettingGroup, SWT.NONE);
        usernameLabel.setText("USERNAME");
        usernameLabel.setBounds(29, 73, 61, 15);
        usernameText = new Text(jdbcSettingGroup, SWT.BORDER);
        usernameText.setText("postgres");
        usernameText.setBounds(96, 70, 318, 21);
        if (!sharedData.getJdbcUsername().isEmpty()) {
            usernameText.setText(sharedData.getJdbcUsername());
        }

        Label passwordLabel = new Label(jdbcSettingGroup, SWT.NONE);
        passwordLabel.setText("PASSWORD");
        passwordLabel.setBounds(29, 100, 62, 15);
        passwordText = new Text(jdbcSettingGroup, SWT.BORDER);
        passwordText.setText("password");
        passwordText.setBounds(96, 97, 318, 21);
        if (!sharedData.getJdbcPassword().isEmpty()) {
            passwordText.setText(sharedData.getJdbcPassword());
        }

        Button dryRunCheckbox = new Button(this, SWT.CHECK);
        dryRunCheckbox.setEnabled(false);
        dryRunCheckbox.setGrayed(true);
        dryRunCheckbox.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        dryRunCheckbox.setBounds(342, 230, 92, 16);
        dryRunCheckbox.setText("Dryrun");

        Button printSqlCheckbox = new Button(this, SWT.CHECK);
        printSqlCheckbox.setEnabled(false);
        printSqlCheckbox.setGrayed(true);
        printSqlCheckbox.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        printSqlCheckbox.setBounds(342, 252, 92, 16);
        printSqlCheckbox.setText("Show SQL");

        Button saveSettingButton = new Button(this, SWT.NONE);
        saveSettingButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                sharedData.setJdbcUrl(urlText.getText().trim());
                sharedData.setJdbcUsername(usernameText.getText().trim());
                sharedData.setJdbcPassword(passwordText.getText().trim());
                sharedData.setTableName(tableNameTxt.getText().trim());
                sharedData.getLogText()
                        .append("Setting updated. URL=" + urlText.getText().trim() + ", USERNAME="
                                + usernameText.getText().trim() + ", PASSWORD="
                                + passwordText.getText().trim() + ", TABLENAME=" + tableNameTxt.getText().trim());
                settingShell.dispose();
            }
        });
        saveSettingButton.setBounds(278, 274, 75, 25);
        saveSettingButton.setText("OK");

        Button cancelSettingButton = new Button(this, SWT.NONE);
        cancelSettingButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                settingShell.close();
                settingShell.dispose();
            }
        });
        cancelSettingButton.setText("Cancel");
        cancelSettingButton.setBounds(359, 274, 75, 25);
        
        Group grpOtherSettings = new Group(this, SWT.NONE);
        grpOtherSettings.setText("Other Settings");
        grpOtherSettings.setBounds(10, 144, 424, 72);
        
        Label lblTableName = new Label(grpOtherSettings, SWT.NONE);
        lblTableName.setText("TABLE NAME");
        lblTableName.setBounds(20, 23, 70, 15);
        
        tableNameTxt = new Text(grpOtherSettings, SWT.BORDER);
        tableNameTxt.setText("meta_proto_delta");
        tableNameTxt.setBounds(96, 20, 318, 21);
        if (!sharedData.getTableName().isEmpty()) {
            tableNameTxt.setText(sharedData.getTableName());
        }
        createShellContents();
    }

    protected void createShellContents() {
        setText("PST Settings");
        setSize(450, 338);
    }
    
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}
