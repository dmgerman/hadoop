begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.files
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|files
package|;
end_package

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
name|Serializable
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|ValidationFailure
import|;
end_import

begin_comment
comment|/**  * Class for single/multiple commit data structures.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|PersistentCommitData
specifier|public
specifier|abstract
class|class
name|PersistentCommitData
implements|implements
name|Serializable
block|{
comment|/**    * Supported version value: {@value}.    * If this is changed the value of {@code serialVersionUID} will change,    * to avoid deserialization problems.    */
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|int
name|VERSION
init|=
literal|1
decl_stmt|;
comment|/**    * Validate the data: those fields which must be non empty, must be set.    * @throws ValidationFailure if the data is invalid    */
DECL|method|validate ()
specifier|public
specifier|abstract
name|void
name|validate
parameter_list|()
throws|throws
name|ValidationFailure
function_decl|;
comment|/**    * Serialize to JSON and then to a byte array, after performing a    * preflight validation of the data to be saved.    * @return the data in a persistable form.    * @throws IOException serialization problem or validation failure.    */
DECL|method|toBytes ()
specifier|public
specifier|abstract
name|byte
index|[]
name|toBytes
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Save to a hadoop filesystem.    * @param fs filesystem    * @param path path    * @param overwrite should any existing file be overwritten    * @throws IOException IO exception    */
DECL|method|save (FileSystem fs, Path path, boolean overwrite)
specifier|public
specifier|abstract
name|void
name|save
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

