package com.ptconsultancy.application.scheduler;

import com.ptconsultancy.messages.MessageHandler;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

@Component
public class Scheduler {

    private MessageHandler messageHandler;
    
    @Autowired
    public Scheduler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
    
    public void start() throws InterruptedException, IOException {

        while (true) {
            System.out.println("Beginning next cycle now");
            processFiles(messageHandler.getMessage("filepaths.incoming.directory"), messageHandler.getMessage("filepaths.done.incoming.directory"));
            Thread.sleep(10000);
        }
    }

    private void processFiles(String srcDir, String targetDir) throws IOException {

        File inDir = new File(srcDir);
        File[] listFiles = inDir.listFiles();

        for (File file : listFiles) {
            FileCopyUtils.copy(file, new File(targetDir + "/" + file.getName()));
            FileUtils.deleteQuietly(new File(file.getAbsolutePath()));
        }
    }
}
