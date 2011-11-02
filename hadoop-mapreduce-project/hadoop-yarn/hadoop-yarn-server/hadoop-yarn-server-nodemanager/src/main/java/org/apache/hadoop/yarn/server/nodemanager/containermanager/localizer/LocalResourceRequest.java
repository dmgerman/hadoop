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
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|LocalResourceType
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
name|LocalResourceVisibility
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
name|util
operator|.
name|ConverterUtils
import|;
end_import

begin_class
DECL|class|LocalResourceRequest
specifier|public
class|class
name|LocalResourceRequest
implements|implements
name|LocalResource
implements|,
name|Comparable
argument_list|<
name|LocalResourceRequest
argument_list|>
block|{
DECL|field|loc
specifier|private
specifier|final
name|Path
name|loc
decl_stmt|;
DECL|field|timestamp
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|LocalResourceType
name|type
decl_stmt|;
DECL|field|visibility
specifier|private
specifier|final
name|LocalResourceVisibility
name|visibility
decl_stmt|;
comment|/**    * Wrap API resource to match against cache of localized resources.    * @param resource Resource requested by container    * @throws URISyntaxException If the path is malformed    */
DECL|method|LocalResourceRequest (LocalResource resource)
specifier|public
name|LocalResourceRequest
parameter_list|(
name|LocalResource
name|resource
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|this
argument_list|(
name|ConverterUtils
operator|.
name|getPathFromYarnURL
argument_list|(
name|resource
operator|.
name|getResource
argument_list|()
argument_list|)
argument_list|,
name|resource
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|resource
operator|.
name|getType
argument_list|()
argument_list|,
name|resource
operator|.
name|getVisibility
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|LocalResourceRequest (Path loc, long timestamp, LocalResourceType type, LocalResourceVisibility visibility)
name|LocalResourceRequest
parameter_list|(
name|Path
name|loc
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|LocalResourceType
name|type
parameter_list|,
name|LocalResourceVisibility
name|visibility
parameter_list|)
block|{
name|this
operator|.
name|loc
operator|=
name|loc
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|visibility
operator|=
name|visibility
expr_stmt|;
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
name|loc
operator|.
name|hashCode
argument_list|()
operator|^
call|(
name|int
call|)
argument_list|(
operator|(
name|timestamp
operator|>>>
literal|32
operator|)
operator|^
name|timestamp
argument_list|)
operator|*
name|type
operator|.
name|hashCode
argument_list|()
return|;
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
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|LocalResourceRequest
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|LocalResourceRequest
name|other
init|=
operator|(
name|LocalResourceRequest
operator|)
name|o
decl_stmt|;
return|return
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
operator|&&
name|getTimestamp
argument_list|()
operator|==
name|other
operator|.
name|getTimestamp
argument_list|()
operator|&&
name|getType
argument_list|()
operator|==
name|other
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (LocalResourceRequest other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|LocalResourceRequest
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|ret
init|=
name|getPath
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|ret
condition|)
block|{
name|ret
operator|=
call|(
name|int
call|)
argument_list|(
name|getTimestamp
argument_list|()
operator|-
name|other
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|ret
condition|)
block|{
name|ret
operator|=
name|getType
argument_list|()
operator|.
name|ordinal
argument_list|()
operator|-
name|other
operator|.
name|getType
argument_list|()
operator|.
name|ordinal
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|getPath ()
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|loc
return|;
block|}
annotation|@
name|Override
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|LocalResourceType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|getResource ()
specifier|public
name|URL
name|getResource
parameter_list|()
block|{
return|return
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
name|loc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
operator|-
literal|1L
return|;
block|}
annotation|@
name|Override
DECL|method|getVisibility ()
specifier|public
name|LocalResourceVisibility
name|getVisibility
parameter_list|()
block|{
return|return
name|visibility
return|;
block|}
annotation|@
name|Override
DECL|method|setResource (URL resource)
specifier|public
name|void
name|setResource
parameter_list|(
name|URL
name|resource
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setSize (long size)
specifier|public
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setTimestamp (long timestamp)
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setType (LocalResourceType type)
specifier|public
name|void
name|setType
parameter_list|(
name|LocalResourceType
name|type
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setVisibility (LocalResourceVisibility visibility)
specifier|public
name|void
name|setVisibility
parameter_list|(
name|LocalResourceVisibility
name|visibility
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{ "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getTimestamp
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

