begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Dynamically assign ids to users/groups as they appear in the external  * filesystem.  */
end_comment

begin_class
DECL|class|FsUGIResolver
specifier|public
class|class
name|FsUGIResolver
extends|extends
name|UGIResolver
block|{
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|field|usernames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|usernames
decl_stmt|;
DECL|field|groupnames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groupnames
decl_stmt|;
DECL|method|FsUGIResolver ()
name|FsUGIResolver
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|id
operator|=
literal|0
expr_stmt|;
name|usernames
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|groupnames
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addUser (String name)
specifier|public
specifier|synchronized
name|void
name|addUser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|usernames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|addUser
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|id
operator|++
expr_stmt|;
name|usernames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addGroup (String name)
specifier|public
specifier|synchronized
name|void
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|groupnames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|addGroup
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|id
operator|++
expr_stmt|;
name|groupnames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

