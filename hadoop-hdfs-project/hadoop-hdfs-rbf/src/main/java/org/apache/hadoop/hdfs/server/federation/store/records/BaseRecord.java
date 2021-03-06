begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * Abstract base of a data record in the StateStore. All StateStore records are  * derived from this class. Data records are persisted in the data store and  * are identified by their primary key. Each data record contains:  *<ul>  *<li>A primary key consisting of a combination of record data fields.  *<li>A modification date.  *<li>A creation date.  *</ul>  */
end_comment

begin_class
DECL|class|BaseRecord
specifier|public
specifier|abstract
class|class
name|BaseRecord
implements|implements
name|Comparable
argument_list|<
name|BaseRecord
argument_list|>
block|{
DECL|field|ERROR_MSG_CREATION_TIME_NEGATIVE
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_MSG_CREATION_TIME_NEGATIVE
init|=
literal|"The creation time for the record cannot be negative."
decl_stmt|;
DECL|field|ERROR_MSG_MODIFICATION_TIME_NEGATIVE
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_MSG_MODIFICATION_TIME_NEGATIVE
init|=
literal|"The modification time for the record cannot be negative."
decl_stmt|;
comment|/**    * Set the modification time for the record.    *    * @param time Modification time of the record.    */
DECL|method|setDateModified (long time)
specifier|public
specifier|abstract
name|void
name|setDateModified
parameter_list|(
name|long
name|time
parameter_list|)
function_decl|;
comment|/**    * Get the modification time for the record.    *    * @return Modification time of the record.    */
DECL|method|getDateModified ()
specifier|public
specifier|abstract
name|long
name|getDateModified
parameter_list|()
function_decl|;
comment|/**    * Set the creation time for the record.    *    * @param time Creation time of the record.    */
DECL|method|setDateCreated (long time)
specifier|public
specifier|abstract
name|void
name|setDateCreated
parameter_list|(
name|long
name|time
parameter_list|)
function_decl|;
comment|/**    * Get the creation time for the record.    *    * @return Creation time of the record    */
DECL|method|getDateCreated ()
specifier|public
specifier|abstract
name|long
name|getDateCreated
parameter_list|()
function_decl|;
comment|/**    * Get the expiration time for the record.    *    * @return Expiration time for the record.    */
DECL|method|getExpirationMs ()
specifier|public
specifier|abstract
name|long
name|getExpirationMs
parameter_list|()
function_decl|;
comment|/**    * Check if this record is expired. The default is false. Override for    * customized behavior.    *    * @return True if the record is expired.    */
DECL|method|isExpired ()
specifier|public
name|boolean
name|isExpired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Get the deletion time for the expired record. The default is disabled.    * Override for customized behavior.    *    * @return Deletion time for the expired record.    */
DECL|method|getDeletionMs ()
specifier|public
name|long
name|getDeletionMs
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Map of primary key names to values for the record. The primary key can be    * a combination of 1-n different State Store serialized values.    *    * @return Map of key/value pairs that constitute this object's primary key.    */
DECL|method|getPrimaryKeys ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPrimaryKeys
parameter_list|()
function_decl|;
comment|/**    * Initialize the object.    */
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
block|{
comment|// Call this after the object has been constructed
name|initDefaultTimes
argument_list|()
expr_stmt|;
block|}
comment|/**    * Initialize default times. The driver may update these timestamps on insert    * and/or update. This should only be called when initializing an object that    * is not backed by a data store.    */
DECL|method|initDefaultTimes ()
specifier|private
name|void
name|initDefaultTimes
parameter_list|()
block|{
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|this
operator|.
name|setDateCreated
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|this
operator|.
name|setDateModified
argument_list|(
name|now
argument_list|)
expr_stmt|;
block|}
comment|/**    * Join the primary keys into one single primary key.    *    * @return A string that is guaranteed to be unique amongst all records of    *         this type.    */
DECL|method|getPrimaryKey ()
specifier|public
name|String
name|getPrimaryKey
parameter_list|()
block|{
return|return
name|generateMashupKey
argument_list|(
name|getPrimaryKeys
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * If the record has fields others than the primary keys. This is used by    * TestStateStoreDriverBase to skip the modification check.    *    * @return If the record has more fields.    */
annotation|@
name|VisibleForTesting
DECL|method|hasOtherFields ()
specifier|public
name|boolean
name|hasOtherFields
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Generates a cache key from a map of values.    *    * @param keys Map of values.    * @return String mashup of key values.    */
DECL|method|generateMashupKey (final Map<String, String> keys)
specifier|protected
specifier|static
name|String
name|generateMashupKey
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keys
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|keys
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Check if this record matches a partial record.    *    * @param other Partial record.    * @return If this record matches.    */
DECL|method|like (BaseRecord other)
specifier|public
name|boolean
name|like
parameter_list|(
name|BaseRecord
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|thisKeys
init|=
name|this
operator|.
name|getPrimaryKeys
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|otherKeys
init|=
name|other
operator|.
name|getPrimaryKeys
argument_list|()
decl_stmt|;
if|if
condition|(
name|thisKeys
operator|==
literal|null
condition|)
block|{
return|return
name|otherKeys
operator|==
literal|null
return|;
block|}
return|return
name|thisKeys
operator|.
name|equals
argument_list|(
name|otherKeys
argument_list|)
return|;
block|}
comment|/**    * Override equals check to use primary key(s) for comparison.    */
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|BaseRecord
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BaseRecord
name|baseObject
init|=
operator|(
name|BaseRecord
operator|)
name|obj
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keyset1
init|=
name|this
operator|.
name|getPrimaryKeys
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keyset2
init|=
name|baseObject
operator|.
name|getPrimaryKeys
argument_list|()
decl_stmt|;
return|return
name|keyset1
operator|.
name|equals
argument_list|(
name|keyset2
argument_list|)
return|;
block|}
comment|/**    * Override hash code to use primary key(s) for comparison.    */
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keyset
init|=
name|this
operator|.
name|getPrimaryKeys
argument_list|()
decl_stmt|;
return|return
name|keyset
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (BaseRecord record)
specifier|public
name|int
name|compareTo
parameter_list|(
name|BaseRecord
name|record
parameter_list|)
block|{
if|if
condition|(
name|record
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|// Descending date order
return|return
call|(
name|int
call|)
argument_list|(
name|record
operator|.
name|getDateModified
argument_list|()
operator|-
name|this
operator|.
name|getDateModified
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Called when the modification time and current time is available, checks for    * expirations.    *    * @param currentTime The current timestamp in ms from the data store, to be    *          compared against the modification and creation dates of the    *          object.    * @return boolean True if the record has been updated and should be    *         committed to the data store. Override for customized behavior.    */
DECL|method|checkExpired (long currentTime)
specifier|public
name|boolean
name|checkExpired
parameter_list|(
name|long
name|currentTime
parameter_list|)
block|{
name|long
name|expiration
init|=
name|getExpirationMs
argument_list|()
decl_stmt|;
name|long
name|modifiedTime
init|=
name|getDateModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|modifiedTime
operator|>
literal|0
operator|&&
name|expiration
operator|>
literal|0
condition|)
block|{
return|return
operator|(
name|modifiedTime
operator|+
name|expiration
operator|)
operator|<
name|currentTime
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Called when this record is expired and expired deletion is enabled, checks    * for the deletion. If an expired record exists beyond the deletion time, it    * should be deleted.    *    * @param currentTime The current timestamp in ms from the data store, to be    *          compared against the modification and creation dates of the    *          object.    * @return boolean True if the record has been updated and should be    *         deleted from the data store.    */
DECL|method|shouldBeDeleted (long currentTime)
specifier|public
name|boolean
name|shouldBeDeleted
parameter_list|(
name|long
name|currentTime
parameter_list|)
block|{
name|long
name|deletionTime
init|=
name|getDeletionMs
argument_list|()
decl_stmt|;
if|if
condition|(
name|isExpired
argument_list|()
operator|&&
name|deletionTime
operator|>
literal|0
condition|)
block|{
name|long
name|elapsedTime
init|=
name|currentTime
operator|-
operator|(
name|getDateModified
argument_list|()
operator|+
name|getExpirationMs
argument_list|()
operator|)
decl_stmt|;
return|return
name|elapsedTime
operator|>
name|deletionTime
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Validates the record. Called when the record is created, populated from the    * state store, and before committing to the state store. If validate failed,    * there throws an exception.    */
DECL|method|validate ()
specifier|public
name|void
name|validate
parameter_list|()
block|{
if|if
condition|(
name|getDateCreated
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ERROR_MSG_CREATION_TIME_NEGATIVE
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|getDateModified
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ERROR_MSG_MODIFICATION_TIME_NEGATIVE
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getPrimaryKey
argument_list|()
return|;
block|}
block|}
end_class

end_unit

