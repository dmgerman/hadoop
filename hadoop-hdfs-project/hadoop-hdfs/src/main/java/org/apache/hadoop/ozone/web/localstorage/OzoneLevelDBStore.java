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
name|fusesource
operator|.
name|leveldbjni
operator|.
name|JniDBFactory
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
name|DB
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
name|Options
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

begin_comment
comment|/**  * OzoneLevelDBStore is used by the local  * OzoneStore which is used in testing.  */
end_comment

begin_class
DECL|class|OzoneLevelDBStore
class|class
name|OzoneLevelDBStore
block|{
DECL|field|db
specifier|private
name|DB
name|db
decl_stmt|;
comment|/**    * Opens a DB file.    *    * @param dbPath - DB File path    * @param createIfMissing - Create if missing    *    * @throws IOException    */
DECL|method|OzoneLevelDBStore (File dbPath, boolean createIfMissing)
name|OzoneLevelDBStore
parameter_list|(
name|File
name|dbPath
parameter_list|,
name|boolean
name|createIfMissing
parameter_list|)
throws|throws
name|IOException
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|createIfMissing
argument_list|(
name|createIfMissing
argument_list|)
expr_stmt|;
name|db
operator|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|dbPath
argument_list|,
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
name|db
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Db is null"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Puts a Key into file.    *    * @param key - key    * @param value - value    */
DECL|method|put (byte[] key, byte[] value)
specifier|public
name|void
name|put
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|db
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get Key.    *    * @param key key    *    * @return value    */
DECL|method|get (byte[] key)
specifier|public
name|byte
index|[]
name|get
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
block|{
return|return
name|db
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Delete Key.    *    * @param key - Key    */
DECL|method|delete (byte[] key)
specifier|public
name|void
name|delete
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
block|{
name|db
operator|.
name|delete
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes the DB.    *    * @throws IOException    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

