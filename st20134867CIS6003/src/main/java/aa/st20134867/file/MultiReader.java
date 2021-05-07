package aa.st20134867.file;

import java.io.File;

public class MultiReader extends FileReader {

    File fp = null;

    public MultiReader(File fp){
        this.fp = fp;
        this.filename = fp.getAbsolutePath();
    }
}

