begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.driver.impl
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
name|driver
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|StandardCharsets
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|StateStoreUtils
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|BaseRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|io
operator|.
name|Files
import|;
end_import

begin_comment
comment|/**  * StateStoreDriver implementation based on a local file.  */
end_comment

begin_class
DECL|class|StateStoreFileImpl
specifier|public
class|class
name|StateStoreFileImpl
extends|extends
name|StateStoreFileBaseImpl
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StateStoreFileImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Configuration keys. */
DECL|field|FEDERATION_STORE_FILE_DIRECTORY
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_FILE_DIRECTORY
init|=
name|DFSConfigKeys
operator|.
name|FEDERATION_STORE_PREFIX
operator|+
literal|"driver.file.directory"
decl_stmt|;
comment|/** Synchronization. */
DECL|field|READ_WRITE_LOCK
specifier|private
specifier|static
specifier|final
name|ReadWriteLock
name|READ_WRITE_LOCK
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
comment|/** Root directory for the state store. */
DECL|field|rootDirectory
specifier|private
name|String
name|rootDirectory
decl_stmt|;
annotation|@
name|Override
DECL|method|exists (String path)
specifier|protected
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|File
name|test
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|test
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|mkdir (String path)
specifier|protected
name|boolean
name|mkdir
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|dir
operator|.
name|mkdirs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRootDir ()
specifier|protected
name|String
name|getRootDir
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|rootDirectory
operator|==
literal|null
condition|)
block|{
name|String
name|dir
init|=
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|FEDERATION_STORE_FILE_DIRECTORY
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|File
name|tempDir
init|=
name|Files
operator|.
name|createTempDir
argument_list|()
decl_stmt|;
name|dir
operator|=
name|tempDir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|rootDirectory
operator|=
name|dir
expr_stmt|;
block|}
return|return
name|this
operator|.
name|rootDirectory
return|;
block|}
annotation|@
name|Override
DECL|method|lockRecordWrite (Class<T> recordClass)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|void
name|lockRecordWrite
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|recordClass
parameter_list|)
block|{
comment|// TODO - Synchronize via FS
name|READ_WRITE_LOCK
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|unlockRecordWrite ( Class<T> recordClass)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|void
name|unlockRecordWrite
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|recordClass
parameter_list|)
block|{
comment|// TODO - Synchronize via FS
name|READ_WRITE_LOCK
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lockRecordRead (Class<T> recordClass)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|void
name|lockRecordRead
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|recordClass
parameter_list|)
block|{
comment|// TODO - Synchronize via FS
name|READ_WRITE_LOCK
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|unlockRecordRead (Class<T> recordClass)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|void
name|unlockRecordRead
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|recordClass
parameter_list|)
block|{
comment|// TODO - Synchronize via FS
name|READ_WRITE_LOCK
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReader ( Class<T> clazz, String sub)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|BufferedReader
name|getReader
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|String
name|sub
parameter_list|)
block|{
name|String
name|filename
init|=
name|StateStoreUtils
operator|.
name|getRecordName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
operator|&&
name|sub
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|filename
operator|+=
literal|"/"
operator|+
name|sub
expr_stmt|;
block|}
name|filename
operator|+=
literal|"/"
operator|+
name|getDataFileName
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading file: {}"
argument_list|,
name|filename
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getRootDir
argument_list|()
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
decl_stmt|;
return|return
name|reader
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot open read stream for record {}"
argument_list|,
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getWriter ( Class<T> clazz, String sub)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|BufferedWriter
name|getWriter
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|String
name|sub
parameter_list|)
block|{
name|String
name|filename
init|=
name|StateStoreUtils
operator|.
name|getRecordName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
operator|&&
name|sub
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|filename
operator|+=
literal|"/"
operator|+
name|sub
expr_stmt|;
block|}
name|filename
operator|+=
literal|"/"
operator|+
name|getDataFileName
argument_list|()
expr_stmt|;
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getRootDir
argument_list|()
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|osw
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|fos
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
name|osw
argument_list|)
decl_stmt|;
return|return
name|writer
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot open read stream for record {}"
argument_list|,
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|setInitialized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

