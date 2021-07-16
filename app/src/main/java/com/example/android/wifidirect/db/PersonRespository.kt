package com.example.android.wifidirect.db

class PersonRespository(private val personDao: PersonDao) {

     fun insert(person: Person){
        personDao.insert(person)
    }

    val allPerson: List<Person> = personDao.getAllPerson()
}