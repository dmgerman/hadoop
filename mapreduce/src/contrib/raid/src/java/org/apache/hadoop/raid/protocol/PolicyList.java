begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.raid.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
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
name|IOException
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
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableFactories
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
name|io
operator|.
name|WritableFactory
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Maintains informatiom about all policies that belong to a category.  * These policies have to be applied one-at-a-time and cannot be run  * simultaneously.  */
end_comment

begin_class
DECL|class|PolicyList
specifier|public
class|class
name|PolicyList
implements|implements
name|Writable
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
literal|"org.apache.hadoop.raid.protocol.PolicyList"
argument_list|)
decl_stmt|;
DECL|field|category
specifier|private
name|List
argument_list|<
name|PolicyInfo
argument_list|>
name|category
decl_stmt|;
comment|// list of policies
DECL|field|srcPath
specifier|private
name|Path
name|srcPath
decl_stmt|;
comment|/**    * Create a new category of policies.    */
DECL|method|PolicyList ()
specifier|public
name|PolicyList
parameter_list|()
block|{
name|this
operator|.
name|category
operator|=
operator|new
name|LinkedList
argument_list|<
name|PolicyInfo
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|srcPath
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Add a new policy to this category.    */
DECL|method|add (PolicyInfo info)
specifier|public
name|void
name|add
parameter_list|(
name|PolicyInfo
name|info
parameter_list|)
block|{
name|category
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|setSrcPath (Configuration conf, String src)
specifier|public
name|void
name|setSrcPath
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|srcPath
operator|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|srcPath
operator|=
name|srcPath
operator|.
name|makeQualified
argument_list|(
name|srcPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getSrcPath ()
specifier|public
name|Path
name|getSrcPath
parameter_list|()
block|{
return|return
name|srcPath
return|;
block|}
comment|/**    * Returns the policies in this category    */
DECL|method|getAll ()
specifier|public
name|Collection
argument_list|<
name|PolicyInfo
argument_list|>
name|getAll
parameter_list|()
block|{
return|return
name|category
return|;
block|}
comment|//////////////////////////////////////////////////
comment|// Writable
comment|//////////////////////////////////////////////////
static|static
block|{
comment|// register a ctor
name|WritableFactories
operator|.
name|setFactory
argument_list|(
name|PolicyList
operator|.
name|class
argument_list|,
operator|new
name|WritableFactory
argument_list|()
block|{
specifier|public
name|Writable
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|PolicyList
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
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
name|writeInt
argument_list|(
name|category
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PolicyInfo
name|p
range|:
name|category
control|)
block|{
name|p
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
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
name|int
name|count
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|PolicyInfo
name|p
init|=
operator|new
name|PolicyInfo
argument_list|()
decl_stmt|;
name|p
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

