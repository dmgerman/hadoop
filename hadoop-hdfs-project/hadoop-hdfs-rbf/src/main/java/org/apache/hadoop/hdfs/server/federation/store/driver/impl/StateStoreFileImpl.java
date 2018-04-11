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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|lang
operator|.
name|ArrayUtils
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
name|router
operator|.
name|RBFConfigKeys
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
name|RBFConfigKeys
operator|.
name|FEDERATION_STORE_PREFIX
operator|+
literal|"driver.file.directory"
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
DECL|method|rename (String src, String dst)
specifier|protected
name|boolean
name|rename
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|)
block|{
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
operator|new
name|File
argument_list|(
name|src
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot rename {} to {}"
argument_list|,
name|src
argument_list|,
name|dst
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|remove (String path)
specifier|protected
name|boolean
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|delete
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"The root directory is not available, using {}"
argument_list|,
name|dir
argument_list|)
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
DECL|method|getReader (String filename)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|BufferedReader
name|getReader
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
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
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
expr_stmt|;
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
name|filename
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|getWriter (String filename)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|BufferedWriter
name|getWriter
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|BufferedWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing file: {}"
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
name|writer
operator|=
operator|new
name|BufferedWriter
argument_list|(
name|osw
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot open write stream for record {}"
argument_list|,
name|filename
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
return|;
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
annotation|@
name|Override
DECL|method|getChildren (String path)
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getChildren
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
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|ArrayUtils
operator|.
name|isNotEmpty
argument_list|(
name|files
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|files
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|String
name|filename
init|=
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|filename
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
end_class

end_unit

