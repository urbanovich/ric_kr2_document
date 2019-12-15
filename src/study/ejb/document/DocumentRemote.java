/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package study.ejb.document;

import java.util.List;
import javax.ejb.Remote;
import study.ejb.document.helpers.Reader;
import study.ejb.document.entity.Document;

/**
 *
 * @author dzmitry
 */
@Remote
public interface DocumentRemote {
    
    /**
     * 
     */
    
    public List<Document> getList();

    Document search(String id);

    boolean add(int id, String title, String content);

    boolean delete(String id);

    void save();
    
}
