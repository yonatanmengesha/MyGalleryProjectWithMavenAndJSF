/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.jotech.video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.event.FileUploadEvent;
import java.io.IOException;

import javax.inject.Named;
//import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

//import javax.faces.bean.ManagedBean;

import java.util.logging.Level;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
//import javax.faces.bean.RequestScoped;

import javax.faces.context.ExternalContext;
import javax.faces.view.ViewScoped;

/**
 *
 * @author yonatan_mengesha
 */
@Named(value = "myGalleryBean")
@ViewScoped

public class MyGalleryBean implements Serializable {

    private List<MyPhoto> photos = new ArrayList<>();
    private static final int BUFFER_SIZE = 6124;

    /**
     * Creates a new instance of MyGalleryBean
     */
    
    public MyGalleryBean() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        Path path = Paths.get(((ServletContext) externalContext.getContext())
                .getRealPath(File.separator + "resources" + File.separator + "photos")
        );
        try ( DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path file : ds) {
                MyPhoto photo = new MyPhoto(file.getFileName().toString(), true);
                photos.add(photo);
            }
        } catch (IOException ex) {
            Logger.getLogger(MyGalleryBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        if (event.getFile() != null) {

            Path path = Paths.get(((ServletContext) externalContext
                    .getContext()).getRealPath(File.separator
                            + "resources" + File.separator + "photos" + File.separator));

            FileOutputStream fileOutputStream;
            InputStream inputStream;

            try {
                String fn = event.getFile().getFileName();
                fileOutputStream = new FileOutputStream(path.toString() + File.separator + fn);
                byte[] buffer = new byte[BUFFER_SIZE];

                int bulk;
                inputStream = event.getFile().getInputstream();

                while (true) {
                    bulk = inputStream.read(buffer);
                    if (bulk < 0) {
                        break;
                    }
                    fileOutputStream.write(buffer, 0, bulk);
                    fileOutputStream.flush();
                }
                fileOutputStream.close();
                inputStream.close();
                MyPhoto new_photo = new MyPhoto(fn, false);
                photos.add(new_photo);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Files were succesfully uploaded",null));
            } catch (FileNotFoundException ex) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Files"+event.getFile(),null));
                Logger.getLogger(MyGalleryBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Files"+event.getFile(),null));
                Logger.getLogger(MyGalleryBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Files Does not exist",null));
            Logger.getLogger(MyGalleryBean.class.getName()).log(Level.SEVERE, "File does not exist", "File does not exist");
        }

    }
    
    


    public List<MyPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<MyPhoto> photos) {
        this.photos = photos;
    }
}
