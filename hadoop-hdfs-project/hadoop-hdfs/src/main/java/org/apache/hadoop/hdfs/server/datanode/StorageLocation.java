begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|classification
operator|.
name|InterfaceAudience
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
name|StorageType
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
name|common
operator|.
name|Util
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
operator|.
name|compile
import|;
end_import

begin_comment
comment|/**  * Encapsulates the URI and storage medium that together describe a  * storage directory.  * The default storage medium is assumed to be DISK, if none is specified.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageLocation
specifier|public
class|class
name|StorageLocation
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|StorageLocation
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|storageType
specifier|final
name|StorageType
name|storageType
decl_stmt|;
DECL|field|file
specifier|final
name|File
name|file
decl_stmt|;
comment|// Regular expression that describes a storage uri with a storage type.
comment|// e.g. [Disk]/storages/storage1/
DECL|field|rawStringRegex
specifier|private
specifier|static
specifier|final
name|String
name|rawStringRegex
init|=
literal|"^\\[(\\w*)\\](.+)$"
decl_stmt|;
DECL|method|StorageLocation (URI uri)
name|StorageLocation
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
name|this
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
DECL|method|StorageLocation (StorageType storageType, URI uri)
name|StorageLocation
parameter_list|(
name|StorageType
name|storageType
parameter_list|,
name|URI
name|uri
parameter_list|)
block|{
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
if|if
condition|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
operator|||
literal|"file"
operator|.
name|equalsIgnoreCase
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
comment|// drop any (illegal) authority in the URI for backwards compatibility
name|this
operator|.
name|file
operator|=
operator|new
name|File
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Got an Unsupported URI schema in "
operator|+
name|uri
operator|+
literal|". Ignoring ..."
argument_list|)
throw|;
block|}
block|}
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|this
operator|.
name|storageType
return|;
block|}
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|file
operator|.
name|toURI
argument_list|()
return|;
block|}
DECL|method|getFile ()
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|this
operator|.
name|file
return|;
block|}
comment|/**    * Attempt to parse a storage uri with storage class and URI. The storage    * class component of the uri is case-insensitive.    *    * @param rawLocation Location string of the format [type]uri, where [type] is    *                    optional.    * @return A StorageLocation object if successfully parsed, null otherwise.    *         Does not throw any exceptions.    */
DECL|method|parse (String rawLocation)
specifier|public
specifier|static
name|StorageLocation
name|parse
parameter_list|(
name|String
name|rawLocation
parameter_list|)
throws|throws
name|IOException
block|{
name|Matcher
name|matcher
init|=
name|compile
argument_list|(
name|rawStringRegex
argument_list|)
operator|.
name|matcher
argument_list|(
name|rawLocation
argument_list|)
decl_stmt|;
name|StorageType
name|storageType
init|=
name|StorageType
operator|.
name|DISK
decl_stmt|;
name|String
name|location
init|=
name|rawLocation
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|classString
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|location
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|classString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|storageType
operator|=
name|StorageType
operator|.
name|valueOf
argument_list|(
name|classString
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to parse storage type: "
operator|+
name|re
operator|.
name|toString
argument_list|()
operator|+
literal|". Using the default storage type for directory "
operator|+
name|location
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|StorageLocation
argument_list|(
name|storageType
argument_list|,
name|Util
operator|.
name|stringAsURI
argument_list|(
name|location
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|storageType
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
operator|+
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

