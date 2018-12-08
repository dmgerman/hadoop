begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security.acl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|security
operator|.
name|acl
package|;
end_package

begin_comment
comment|/**  * Default implementation for {@link IAccessAuthorizer}.  * */
end_comment

begin_class
DECL|class|OzoneAccessAuthorizer
specifier|public
class|class
name|OzoneAccessAuthorizer
implements|implements
name|IAccessAuthorizer
block|{
annotation|@
name|Override
DECL|method|checkAccess (IOzoneObj ozoneObject, RequestContext context)
specifier|public
name|boolean
name|checkAccess
parameter_list|(
name|IOzoneObj
name|ozoneObject
parameter_list|,
name|RequestContext
name|context
parameter_list|)
throws|throws
name|OzoneAclException
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

