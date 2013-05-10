begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
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
name|localizer
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResource
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
name|api
operator|.
name|records
operator|.
name|URL
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
name|api
operator|.
name|records
operator|.
name|SerializedException
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
name|api
operator|.
name|protocolrecords
operator|.
name|LocalResourceStatus
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
name|api
operator|.
name|protocolrecords
operator|.
name|ResourceStatusType
import|;
end_import

begin_class
DECL|class|MockLocalResourceStatus
specifier|public
class|class
name|MockLocalResourceStatus
implements|implements
name|LocalResourceStatus
block|{
DECL|field|rsrc
specifier|private
name|LocalResource
name|rsrc
init|=
literal|null
decl_stmt|;
DECL|field|tag
specifier|private
name|ResourceStatusType
name|tag
init|=
literal|null
decl_stmt|;
DECL|field|localPath
specifier|private
name|URL
name|localPath
init|=
literal|null
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|ex
specifier|private
name|SerializedException
name|ex
init|=
literal|null
decl_stmt|;
DECL|method|MockLocalResourceStatus ()
name|MockLocalResourceStatus
parameter_list|()
block|{ }
DECL|method|MockLocalResourceStatus (LocalResource rsrc, ResourceStatusType tag, URL localPath, SerializedException ex)
name|MockLocalResourceStatus
parameter_list|(
name|LocalResource
name|rsrc
parameter_list|,
name|ResourceStatusType
name|tag
parameter_list|,
name|URL
name|localPath
parameter_list|,
name|SerializedException
name|ex
parameter_list|)
block|{
name|this
operator|.
name|rsrc
operator|=
name|rsrc
expr_stmt|;
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
name|this
operator|.
name|localPath
operator|=
name|localPath
expr_stmt|;
name|this
operator|.
name|ex
operator|=
name|ex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResource ()
specifier|public
name|LocalResource
name|getResource
parameter_list|()
block|{
return|return
name|rsrc
return|;
block|}
annotation|@
name|Override
DECL|method|getStatus ()
specifier|public
name|ResourceStatusType
name|getStatus
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalSize ()
specifier|public
name|long
name|getLocalSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalPath ()
specifier|public
name|URL
name|getLocalPath
parameter_list|()
block|{
return|return
name|localPath
return|;
block|}
annotation|@
name|Override
DECL|method|getException ()
specifier|public
name|SerializedException
name|getException
parameter_list|()
block|{
return|return
name|ex
return|;
block|}
annotation|@
name|Override
DECL|method|setResource (LocalResource rsrc)
specifier|public
name|void
name|setResource
parameter_list|(
name|LocalResource
name|rsrc
parameter_list|)
block|{
name|this
operator|.
name|rsrc
operator|=
name|rsrc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setStatus (ResourceStatusType tag)
specifier|public
name|void
name|setStatus
parameter_list|(
name|ResourceStatusType
name|tag
parameter_list|)
block|{
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLocalPath (URL localPath)
specifier|public
name|void
name|setLocalPath
parameter_list|(
name|URL
name|localPath
parameter_list|)
block|{
name|this
operator|.
name|localPath
operator|=
name|localPath
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLocalSize (long size)
specifier|public
name|void
name|setLocalSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setException (SerializedException ex)
specifier|public
name|void
name|setException
parameter_list|(
name|SerializedException
name|ex
parameter_list|)
block|{
name|this
operator|.
name|ex
operator|=
name|ex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|MockLocalResourceStatus
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|MockLocalResourceStatus
name|other
init|=
operator|(
name|MockLocalResourceStatus
operator|)
name|o
decl_stmt|;
return|return
name|getResource
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getResource
argument_list|()
argument_list|)
operator|&&
name|getStatus
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|&&
operator|(
literal|null
operator|!=
name|getLocalPath
argument_list|()
operator|&&
name|getLocalPath
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getLocalPath
argument_list|()
argument_list|)
operator|)
operator|&&
operator|(
literal|null
operator|!=
name|getException
argument_list|()
operator|&&
name|getException
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getException
argument_list|()
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|4344
return|;
block|}
block|}
end_class

end_unit

