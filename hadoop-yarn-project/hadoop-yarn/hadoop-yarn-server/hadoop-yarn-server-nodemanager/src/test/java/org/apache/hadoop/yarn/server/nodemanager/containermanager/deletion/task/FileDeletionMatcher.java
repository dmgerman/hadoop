begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.deletion.task
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|task
package|;
end_package

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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|DeletionService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatcher
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

begin_comment
comment|/**  * ArgumentMatcher to check the arguments of the {@link FileDeletionTask}.  */
end_comment

begin_class
DECL|class|FileDeletionMatcher
specifier|public
class|class
name|FileDeletionMatcher
extends|extends
name|ArgumentMatcher
argument_list|<
name|FileDeletionTask
argument_list|>
block|{
DECL|field|delService
specifier|private
specifier|final
name|DeletionService
name|delService
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|subDirIncludes
specifier|private
specifier|final
name|Path
name|subDirIncludes
decl_stmt|;
DECL|field|baseDirIncludes
specifier|private
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|baseDirIncludes
decl_stmt|;
DECL|method|FileDeletionMatcher (DeletionService delService, String user, Path subDirIncludes, List<Path> baseDirIncludes)
specifier|public
name|FileDeletionMatcher
parameter_list|(
name|DeletionService
name|delService
parameter_list|,
name|String
name|user
parameter_list|,
name|Path
name|subDirIncludes
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|baseDirIncludes
parameter_list|)
block|{
name|this
operator|.
name|delService
operator|=
name|delService
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|subDirIncludes
operator|=
name|subDirIncludes
expr_stmt|;
name|this
operator|.
name|baseDirIncludes
operator|=
name|baseDirIncludes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches (Object o)
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|FileDeletionTask
name|fd
init|=
operator|(
name|FileDeletionTask
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|fd
operator|.
name|getUser
argument_list|()
operator|==
literal|null
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|fd
operator|.
name|getUser
argument_list|()
operator|!=
literal|null
operator|&&
name|user
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|fd
operator|.
name|getUser
argument_list|()
operator|!=
literal|null
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
return|return
name|fd
operator|.
name|getUser
argument_list|()
operator|.
name|equals
argument_list|(
name|user
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|comparePaths
argument_list|(
name|fd
operator|.
name|getSubDir
argument_list|()
argument_list|,
name|subDirIncludes
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|baseDirIncludes
operator|==
literal|null
operator|&&
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|baseDirIncludes
operator|!=
literal|null
operator|&&
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|baseDirIncludes
operator|!=
literal|null
operator|&&
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|baseDirIncludes
operator|.
name|size
argument_list|()
operator|!=
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|baseDirIncludes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|comparePaths
argument_list|(
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|baseDirIncludes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|comparePaths (Path p1, String p2)
specifier|public
name|boolean
name|comparePaths
parameter_list|(
name|Path
name|p1
parameter_list|,
name|String
name|p2
parameter_list|)
block|{
if|if
condition|(
name|p1
operator|==
literal|null
operator|&&
name|p2
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|p1
operator|!=
literal|null
operator|&&
name|p2
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|p1
operator|!=
literal|null
operator|&&
name|p2
operator|!=
literal|null
condition|)
block|{
return|return
name|p1
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|contains
argument_list|(
name|p2
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

