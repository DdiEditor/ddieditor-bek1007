package dk.dda.ddieditor.bek1007.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
 
public class NewSnippet {
 
    public static void main( String [] args ) {
 
        // Display + shell
        Display display = new Display ();
        Shell shell = new Shell( display );
        shell.setLayout( new GridLayout( 2, false ));
 
        // Load an image
        //ImageData imgData = new ImageData( "your image path" );
        ImageData imgData = new ImageData( "/home/ddajvj/Desktop/Sith Happens.jpg" );
        Image image = new Image( display, imgData );
 
        // The scrolled composite
        ScrolledComposite sc = new ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
        GridData layoutData = new GridData( GridData.FILL_HORIZONTAL );
        layoutData.horizontalSpan = 2;
        layoutData.heightHint = 400;
        sc.setLayoutData( layoutData );
 
        Label imgLabel = new Label( sc, SWT.NONE );
        imgLabel.setImage( image );
        imgLabel.setSize( imgLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ));
        sc.setContent( imgLabel );
 
        Label l = new Label( shell, SWT.NONE );
        l.setText( "Nom actuel :" );
        layoutData = new GridData();
        layoutData.verticalIndent = 20;
        l.setLayoutData( layoutData );
 
        Text text = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY );
        text.setText( "The image name" );
        layoutData = new GridData( GridData.FILL_HORIZONTAL );
        layoutData.verticalIndent = 20;
        text.setLayoutData( layoutData );
 
        // Run it
        shell.setSize( 800, 600 );
        shell.open();
        while( ! shell.isDisposed()) {
            if( ! display.readAndDispatch())
                display.sleep();
        }
 
        display.dispose();
        image.dispose();
    }
}