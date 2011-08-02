begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

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
name|DataInput
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

begin_comment
comment|/** A base class for Writables that provides version checking.  *  *<p>This is useful when a class may evolve, so that instances written by the  * old version of the class may still be processed by the new version.  To  * handle this situation, {@link #readFields(DataInput)}  * implementations should catch {@link VersionMismatchException}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|VersionedWritable
specifier|public
specifier|abstract
class|class
name|VersionedWritable
implements|implements
name|Writable
block|{
comment|/** Return the version number of the current implementation. */
DECL|method|getVersion ()
specifier|public
specifier|abstract
name|byte
name|getVersion
parameter_list|()
function_decl|;
comment|// javadoc from Writable
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
name|writeByte
argument_list|(
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// store version
block|}
comment|// javadoc from Writable
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
name|byte
name|version
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
comment|// read version
if|if
condition|(
name|version
operator|!=
name|getVersion
argument_list|()
condition|)
throw|throw
operator|new
name|VersionMismatchException
argument_list|(
name|getVersion
argument_list|()
argument_list|,
name|version
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

