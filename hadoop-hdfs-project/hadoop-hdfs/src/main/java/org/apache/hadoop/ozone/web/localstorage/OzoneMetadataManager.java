begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.localstorage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|localstorage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ozone
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|web
operator|.
name|exceptions
operator|.
name|ErrorTable
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
name|ozone
operator|.
name|web
operator|.
name|exceptions
operator|.
name|OzoneException
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
name|ozone
operator|.
name|web
operator|.
name|handlers
operator|.
name|UserArgs
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
name|ozone
operator|.
name|web
operator|.
name|handlers
operator|.
name|VolumeArgs
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
name|ozone
operator|.
name|web
operator|.
name|response
operator|.
name|ListVolumes
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
name|ozone
operator|.
name|web
operator|.
name|response
operator|.
name|VolumeInfo
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
name|ozone
operator|.
name|web
operator|.
name|response
operator|.
name|VolumeOwner
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
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneConsts
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * A stand alone Ozone implementation that allows us to run  * Ozone tests in local mode. This acts as the  * ozone backend when using MiniDFSCluster for testing.  */
end_comment

begin_class
DECL|class|OzoneMetadataManager
specifier|public
specifier|final
class|class
name|OzoneMetadataManager
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|OzoneMetadataManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bm
specifier|private
specifier|static
name|OzoneMetadataManager
name|bm
init|=
literal|null
decl_stmt|;
comment|/*     OzoneMetadataManager manages volume/bucket/object metadata and     data.      Metadata is maintained in 2 level DB files, UserDB and MetadataDB.      UserDB contains a Name and a List. For example volumes owned by the user     bilbo, would be maintained in UserDB as {bilbo}->{shire, rings}      This list part of mapping is context sensitive.  That is, if you use {user     name} as the key, the list you get is a list of volumes. if you use     {user/volume} as the key the list you get is list of buckets. if you use     {user/volume/bucket} as key the list you get is the list of objects.      All keys in the UserDB starts with the UserName.      We also need to maintain a flat namespace for volumes. This is     maintained by the MetadataDB. MetadataDB contains the name of an     object(volume, bucket or key) and its associated metadata.     The keys in the Metadata DB are {volume}, {volume/bucket} or     {volume/bucket/key}. User name is absent, so we have a common root name     space for the volume.      The value of part of metadataDB points to corresponding *Info structures.     {volume] -> volumeInfo     {volume/bucket} -> bucketInfo     {volume/bucket/key} -> keyInfo       Here are various work flows :      CreateVolume -> Check if Volume exists in metadataDB, if not update UserDB     with a list of volumes and update metadataDB with VolumeInfo.      DeleteVolume -> Check the Volume, and check the VolumeInfo->bucketCount.     if bucketCount == 0, delete volume from userDB->{List of volumes} and     metadataDB.      Very similar work flows exist for CreateBucket and DeleteBucket.        // Please note : These database operations are *not* transactional,       // which means that failure can lead to inconsistencies.       // Only way to recover is to reset to a clean state, or       // use rm -rf /tmp/ozone :)      We have very simple locking policy. We have a ReaderWriter lock that is     taken for each action, this lock is aptly named "lock".      All actions *must* be performed with a lock held, either a read     lock or a write lock. Violation of these locking policies can be harmful.         // // IMPORTANT :       // //  This is a simulation layer, this is NOT how the real       // //  OZONE functions. This is written to so that we can write       // //  stand-alone tests for the protocol and client code.  */
DECL|field|userDB
specifier|private
name|OzoneLevelDBStore
name|userDB
decl_stmt|;
DECL|field|metadataDB
specifier|private
name|OzoneLevelDBStore
name|metadataDB
decl_stmt|;
DECL|field|USER_DB
specifier|private
specifier|static
specifier|final
name|String
name|USER_DB
init|=
literal|"/user.db"
decl_stmt|;
DECL|field|META_DB
specifier|private
specifier|static
specifier|final
name|String
name|META_DB
init|=
literal|"/metadata.db"
decl_stmt|;
DECL|field|lock
specifier|private
name|ReadWriteLock
name|lock
decl_stmt|;
DECL|field|encoding
specifier|private
name|Charset
name|encoding
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|/**    * Constructs OzoneMetadataManager.    */
DECL|method|OzoneMetadataManager ()
specifier|private
name|OzoneMetadataManager
parameter_list|()
block|{
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|String
name|storageRoot
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_STORAGE_LOCAL_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_STORAGE_LOCAL_ROOT_DEFAULT
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|storageRoot
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Creation of Ozone root failed. "
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|userDB
operator|=
operator|new
name|OzoneLevelDBStore
argument_list|(
operator|new
name|File
argument_list|(
name|storageRoot
operator|+
name|USER_DB
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|metadataDB
operator|=
operator|new
name|OzoneLevelDBStore
argument_list|(
operator|new
name|File
argument_list|(
name|storageRoot
operator|+
name|META_DB
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Cannot open db :"
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gets Ozone Manager.    * @return OzoneMetadataManager    */
DECL|method|getOzoneMetadataManager ()
specifier|public
specifier|static
specifier|synchronized
name|OzoneMetadataManager
name|getOzoneMetadataManager
parameter_list|()
block|{
if|if
condition|(
name|bm
operator|==
literal|null
condition|)
block|{
name|bm
operator|=
operator|new
name|OzoneMetadataManager
argument_list|()
expr_stmt|;
block|}
return|return
name|bm
return|;
block|}
comment|/**    * Creates a volume.    *    * @param args - VolumeArgs    *    * @throws OzoneException    */
DECL|method|createVolume (VolumeArgs args)
specifier|public
name|void
name|createVolume
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
try|try
block|{
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_DATE_FORMAT
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|format
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_TIME_ZONE
argument_list|)
argument_list|)
expr_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|byte
index|[]
name|volumeName
init|=
name|metadataDB
operator|.
name|get
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumeName
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Volume already exists."
argument_list|)
expr_stmt|;
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_ALREADY_EXISTS
argument_list|,
name|args
argument_list|)
throw|;
block|}
name|VolumeInfo
name|newVInfo
init|=
operator|new
name|VolumeInfo
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|format
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|args
operator|.
name|getAdminName
argument_list|()
argument_list|)
decl_stmt|;
name|newVInfo
operator|.
name|setQuota
argument_list|(
name|args
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|VolumeOwner
name|owner
init|=
operator|new
name|VolumeOwner
argument_list|(
name|args
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
name|newVInfo
operator|.
name|setOwner
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|ListVolumes
name|volumeList
decl_stmt|;
name|byte
index|[]
name|userVolumes
init|=
name|userDB
operator|.
name|get
argument_list|(
name|args
operator|.
name|getUserName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|userVolumes
operator|==
literal|null
condition|)
block|{
name|volumeList
operator|=
operator|new
name|ListVolumes
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|volumeList
operator|=
name|ListVolumes
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|userVolumes
argument_list|,
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|volumeList
operator|.
name|addVolume
argument_list|(
name|newVInfo
argument_list|)
expr_stmt|;
name|volumeList
operator|.
name|sort
argument_list|()
expr_stmt|;
comment|// Please note : These database operations are *not* transactional,
comment|// which means that failure can lead to inconsistencies.
comment|// Only way to recover is to reset to a clean state, or
comment|// use rm -rf /tmp/ozone :)
name|userDB
operator|.
name|put
argument_list|(
name|args
operator|.
name|getUserName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|,
name|volumeList
operator|.
name|toDBString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
name|metadataDB
operator|.
name|put
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|,
name|newVInfo
operator|.
name|toDBString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Updates the Volume properties like Owner Name and Quota.    *    * @param args - Volume Args    * @param property - Flag which tells us what property to upgrade    *    * @throws OzoneException    */
DECL|method|setVolumeProperty (VolumeArgs args, VolumeProperty property)
specifier|public
name|void
name|setVolumeProperty
parameter_list|(
name|VolumeArgs
name|args
parameter_list|,
name|VolumeProperty
name|property
parameter_list|)
throws|throws
name|OzoneException
block|{
name|VolumeInfo
name|info
decl_stmt|;
try|try
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|byte
index|[]
name|volumeInfo
init|=
name|metadataDB
operator|.
name|get
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumeInfo
operator|==
literal|null
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_NOT_FOUND
argument_list|,
name|args
argument_list|)
throw|;
block|}
name|info
operator|=
name|VolumeInfo
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|volumeInfo
argument_list|,
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|userBytes
init|=
name|userDB
operator|.
name|get
argument_list|(
name|args
operator|.
name|getResourceName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
name|ListVolumes
name|volumeList
decl_stmt|;
if|if
condition|(
name|userBytes
operator|==
literal|null
condition|)
block|{
name|volumeList
operator|=
operator|new
name|ListVolumes
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|volumeList
operator|=
name|ListVolumes
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|userBytes
argument_list|,
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|property
condition|)
block|{
case|case
name|OWNER
case|:
comment|// needs new owner, we delete the volume object from the
comment|// old user's volume list
name|removeOldOwner
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|VolumeOwner
name|owner
init|=
operator|new
name|VolumeOwner
argument_list|(
name|args
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
comment|// set the new owner
name|info
operator|.
name|setOwner
argument_list|(
name|owner
argument_list|)
expr_stmt|;
break|break;
case|case
name|QUOTA
case|:
comment|// if this is quota update we just remove the old object from the
comment|// current users list and update the same object later.
name|volumeList
operator|.
name|getVolumes
argument_list|()
operator|.
name|remove
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|info
operator|.
name|setQuota
argument_list|(
name|args
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|OzoneException
name|ozEx
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ozEx
operator|.
name|setMessage
argument_list|(
literal|"Volume property is not recognized"
argument_list|)
expr_stmt|;
throw|throw
name|ozEx
throw|;
block|}
name|volumeList
operator|.
name|addVolume
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|metadataDB
operator|.
name|put
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|,
name|info
operator|.
name|toDBString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
comment|// if this is an owner change this put will create a new owner or update
comment|// the owner's volume list.
name|userDB
operator|.
name|put
argument_list|(
name|args
operator|.
name|getResourceName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|,
name|volumeList
operator|.
name|toDBString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Removes the old owner from the volume.    *    * @param info - VolumeInfo    *    * @throws IOException    */
DECL|method|removeOldOwner (VolumeInfo info)
specifier|private
name|void
name|removeOldOwner
parameter_list|(
name|VolumeInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We need to look the owner that we know is the current owner
name|byte
index|[]
name|volumeBytes
init|=
name|userDB
operator|.
name|get
argument_list|(
name|info
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
name|ListVolumes
name|volumeList
init|=
name|ListVolumes
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|volumeBytes
argument_list|,
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
name|volumeList
operator|.
name|getVolumes
argument_list|()
operator|.
name|remove
argument_list|(
name|info
argument_list|)
expr_stmt|;
comment|// Write the new list info to the old user data
name|userDB
operator|.
name|put
argument_list|(
name|info
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|,
name|volumeList
operator|.
name|toDBString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks if you are the owner of a specific volume.    *    * @param args - VolumeArgs    *    * @return - True if you are the owner, false otherwise    *    * @throws OzoneException    */
DECL|method|checkVolumeAccess (VolumeArgs args)
specifier|public
name|boolean
name|checkVolumeAccess
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
name|VolumeInfo
name|info
decl_stmt|;
try|try
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|byte
index|[]
name|volumeInfo
init|=
name|metadataDB
operator|.
name|get
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumeInfo
operator|==
literal|null
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_NOT_FOUND
argument_list|,
name|args
argument_list|)
throw|;
block|}
name|info
operator|=
name|VolumeInfo
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|volumeInfo
argument_list|,
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|info
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|args
operator|.
name|getUserName
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * getVolumeInfo returns the Volume Info of a specific volume.    *    * @param args - Volume args    *    * @return VolumeInfo    *    * @throws OzoneException    */
DECL|method|getVolumeInfo (VolumeArgs args)
specifier|public
name|VolumeInfo
name|getVolumeInfo
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
try|try
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|byte
index|[]
name|volumeInfo
init|=
name|metadataDB
operator|.
name|get
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumeInfo
operator|==
literal|null
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_NOT_FOUND
argument_list|,
name|args
argument_list|)
throw|;
block|}
return|return
name|VolumeInfo
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|volumeInfo
argument_list|,
name|encoding
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns all the volumes owned by a specific user.    *    * @param args - User Args    *    * @return - ListVolumes    *    * @throws OzoneException    */
DECL|method|listVolumes (UserArgs args)
specifier|public
name|ListVolumes
name|listVolumes
parameter_list|(
name|UserArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
try|try
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|byte
index|[]
name|volumeList
init|=
name|userDB
operator|.
name|get
argument_list|(
name|args
operator|.
name|getUserName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumeList
operator|==
literal|null
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|USER_NOT_FOUND
argument_list|,
name|args
argument_list|)
throw|;
block|}
return|return
name|ListVolumes
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|volumeList
argument_list|,
name|encoding
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Deletes a volume if it exists and is empty.    *    * @param args - volume args    *    * @throws OzoneException    */
DECL|method|deleteVolume (VolumeArgs args)
specifier|public
name|void
name|deleteVolume
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
try|try
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|byte
index|[]
name|volumeName
init|=
name|metadataDB
operator|.
name|get
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumeName
operator|==
literal|null
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_NOT_FOUND
argument_list|,
name|args
argument_list|)
throw|;
block|}
name|VolumeInfo
name|vInfo
init|=
name|VolumeInfo
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|volumeName
argument_list|,
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
comment|// Only remove volumes if they are empty.
if|if
condition|(
name|vInfo
operator|.
name|getBucketCount
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_NOT_EMPTY
argument_list|,
name|args
argument_list|)
throw|;
block|}
name|ListVolumes
name|volumeList
decl_stmt|;
name|String
name|user
init|=
name|vInfo
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|byte
index|[]
name|userVolumes
init|=
name|userDB
operator|.
name|get
argument_list|(
name|user
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|userVolumes
operator|==
literal|null
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_NOT_FOUND
argument_list|,
name|args
argument_list|)
throw|;
block|}
name|volumeList
operator|=
name|ListVolumes
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|userVolumes
argument_list|,
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
name|volumeList
operator|.
name|getVolumes
argument_list|()
operator|.
name|remove
argument_list|(
name|vInfo
argument_list|)
expr_stmt|;
name|metadataDB
operator|.
name|delete
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
name|userDB
operator|.
name|put
argument_list|(
name|user
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|,
name|volumeList
operator|.
name|toDBString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * This is used in updates to volume metadata.    */
DECL|enum|VolumeProperty
specifier|public
enum|enum
name|VolumeProperty
block|{
DECL|enumConstant|OWNER
DECL|enumConstant|QUOTA
name|OWNER
block|,
name|QUOTA
block|}
block|}
end_class

end_unit

