package DAO.Web.WebHelper

import DAO.Web.PersonGroups
import DAO.Web.Persons
import io.reactivex.Completable
import io.reactivex.Observable
import netscape.javascript.JSObject
import retrofit2.Response
import retrofit2.http.*

interface Api {

    /* ---------- Person ---------- */
    //POST
    @POST("persons")
    fun createUser(@Query("pfirstname") pfirstname:String): Observable<String>

    @POST("persons")
    fun createUser(@Query("pfirstname") pfirstname:String, @QueryMap params: Map<String, String>): Observable<String>

    //GET
    @GET("{module}/info")
    fun getInfo(@Path("module") module: String): Observable<Response<Any>>

    @GET("persons/{personId}")
    fun getUserById(@Path("personId") personId: String): Observable<Persons>

    @GET("persons")
    fun getUsers(@QueryMap params: Map<String, String>): Observable<List<Persons>>

    //PUT

    //DELETE
    @DELETE("persons/{personId}")
    fun delUser(@Path("personId") personId: String): Observable<String>


    /* ---------- Group ---------- */
    //POST
    @POST("personGroups")
    fun createGroup(@Query("gcname") gcname: String): Observable<String>

    @POST("personGroups")
    fun createGroup(@Query("gcname") gcname: String, @QueryMap params: Map<String, String>): Observable<String>

    @POST("personGroups/{id}/childGroups/{childId}")
    fun addChildGroup(@Path("id") id: String, @Path("childId") childId: String): Observable<Int>

    @POST("personGroups/{id}/childPersons/{personId}")
    fun addPersonToGroup(@Path("id") id: String, @Path("personId") personId: String): Observable<Any>

    //GET
    @GET("personGroups/{id}")
    fun getGroupById(@Path("id") id: String): Observable<PersonGroups>

    @GET("personGroups")
    fun getGroup(@QueryMap params: Map<String, String>): Observable<List<PersonGroups>>

    @GET("personGroups/{id}/childGroups")
    fun getChildGroups(@Path("id") id: String): Observable<List<PersonGroups>>

    @GET("personGroups/{id}/childPersons")
    fun getPersonsFromGroup(@Path("id") id: String): Observable<List<Persons>>

    //PUT
    @PUT("personGroups/{id}")
    fun updateGroup(@Path("id") id: String, @QueryMap params: Map<String, String>): Observable<PersonGroups>

    //DELETE
    @DELETE("personGroups/{id}/childPersons/{personId}")
    fun removePersonFromGroup(@Path("id") id: String, @Path("personId") personId: String): Observable<Any>

}