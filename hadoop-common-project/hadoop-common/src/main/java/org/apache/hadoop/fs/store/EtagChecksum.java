begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.store
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|StandardCharsets
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
name|FileChecksum
import|;
end_import

begin_comment
comment|/**  * An etag as a checksum.  * Consider these suitable for checking if an object has changed, but  * not suitable for comparing two different objects for equivalence,  * especially between object stores.  */
end_comment

begin_class
DECL|class|EtagChecksum
specifier|public
class|class
name|EtagChecksum
extends|extends
name|FileChecksum
block|{
comment|/** The algorithm name: {@value}. */
DECL|field|ETAG
specifier|private
specifier|static
specifier|final
name|String
name|ETAG
init|=
literal|"etag"
decl_stmt|;
comment|/**    * Etag string.    */
DECL|field|eTag
specifier|private
name|String
name|eTag
init|=
literal|""
decl_stmt|;
comment|/**    * Create with an empty etag.    */
DECL|method|EtagChecksum ()
specifier|public
name|EtagChecksum
parameter_list|()
block|{   }
comment|/**    * Create with a string etag.    * @param eTag etag    */
DECL|method|EtagChecksum (String eTag)
specifier|public
name|EtagChecksum
parameter_list|(
name|String
name|eTag
parameter_list|)
block|{
name|this
operator|.
name|eTag
operator|=
name|eTag
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAlgorithmName ()
specifier|public
name|String
name|getAlgorithmName
parameter_list|()
block|{
return|return
name|ETAG
return|;
block|}
annotation|@
name|Override
DECL|method|getLength ()
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|eTag
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|getBytes ()
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|eTag
operator|!=
literal|null
condition|?
name|eTag
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
else|:
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|eTag
operator|!=
literal|null
condition|?
name|eTag
else|:
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|eTag
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
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
literal|"etag: \""
operator|+
name|eTag
operator|+
literal|'"'
return|;
block|}
block|}
end_class

end_unit

