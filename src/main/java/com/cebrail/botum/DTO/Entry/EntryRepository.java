package com.cebrail.botum.DTO.Entry;

import com.cebrail.botum.Model.Entry;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public interface EntryRepository {


    //create
    void saveEntry(Entry entry);
    void saveAllEntries(List<Entry> entryList);


    //read
    //Entry getEntry(String EntryId) ;
    //List<Entry> getEntries(String userChatId);


    Entry getEntryBy(String columnType, Object value, String userChatId);

    Entry getEntryById(Long EntryId, String userChatId);
    Entry getEntryByKelime(String kelime, String userChatId);
    Entry getEntryByAnlam(String anlam, String userChatId);

    List<Entry> getEntriesByUserChatId(String userChatId);

    //update
    void updateEntry(Entry entry);


    //delete
    Integer deleteEntry(Entry entry);
    Integer deleteEntryByEntryId(Integer entryId);
    Integer deleteAllEntriesOfUserId(String userChatId);
    Integer deleteEntries(List<Entry> entries);
    Integer deleteEntryByKelime(String kelime, String userChatId);

    //other operations
    boolean exists(String kelime, String userChatId);

    Integer size(String userChatId);
}
