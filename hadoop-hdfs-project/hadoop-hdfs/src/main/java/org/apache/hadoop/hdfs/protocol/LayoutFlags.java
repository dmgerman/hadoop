begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|base
operator|.
name|Preconditions
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
name|collect
operator|.
name|ImmutableSet
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
name|collect
operator|.
name|Sets
import|;
end_import

begin_comment
comment|/**  * LayoutFlags represent features which the FSImage and edit logs can either  * support or not, independently of layout version.  *   * Note: all flags starting with 'test' are reserved for unit test purposes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|LayoutFlags
specifier|public
class|class
name|LayoutFlags
block|{
comment|/**    * Load a LayoutFlags object from a stream.    *    * @param in            The stream to read from.    * @throws IOException    */
DECL|method|read (DataInputStream in)
specifier|public
specifier|static
name|LayoutFlags
name|read
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The length of the feature flag section "
operator|+
literal|"was negative at "
operator|+
name|length
operator|+
literal|" bytes."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Found feature flags which we can't handle. "
operator|+
literal|"Please upgrade your software."
argument_list|)
throw|;
block|}
return|return
operator|new
name|LayoutFlags
argument_list|()
return|;
block|}
DECL|method|LayoutFlags ()
specifier|private
name|LayoutFlags
parameter_list|()
block|{   }
DECL|method|write (DataOutputStream out)
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

