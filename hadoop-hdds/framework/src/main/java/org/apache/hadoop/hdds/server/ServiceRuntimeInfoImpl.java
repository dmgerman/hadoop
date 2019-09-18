begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
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
name|hdds
operator|.
name|utils
operator|.
name|VersionInfo
import|;
end_import

begin_comment
comment|/**  * Helper base class to report the standard version and runtime information.  *  */
end_comment

begin_class
DECL|class|ServiceRuntimeInfoImpl
specifier|public
class|class
name|ServiceRuntimeInfoImpl
implements|implements
name|ServiceRuntimeInfo
block|{
DECL|field|startedTimeInMillis
specifier|private
name|long
name|startedTimeInMillis
decl_stmt|;
DECL|field|versionInfo
specifier|private
specifier|final
name|VersionInfo
name|versionInfo
decl_stmt|;
DECL|method|ServiceRuntimeInfoImpl (VersionInfo versionInfo)
specifier|protected
name|ServiceRuntimeInfoImpl
parameter_list|(
name|VersionInfo
name|versionInfo
parameter_list|)
block|{
name|this
operator|.
name|versionInfo
operator|=
name|versionInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|versionInfo
operator|.
name|getVersion
argument_list|()
operator|+
literal|", r"
operator|+
name|versionInfo
operator|.
name|getRevision
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSoftwareVersion ()
specifier|public
name|String
name|getSoftwareVersion
parameter_list|()
block|{
return|return
name|versionInfo
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCompileInfo ()
specifier|public
name|String
name|getCompileInfo
parameter_list|()
block|{
return|return
name|versionInfo
operator|.
name|getDate
argument_list|()
operator|+
literal|" by "
operator|+
name|versionInfo
operator|.
name|getUser
argument_list|()
operator|+
literal|" from "
operator|+
name|versionInfo
operator|.
name|getBranch
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getStartedTimeInMillis ()
specifier|public
name|long
name|getStartedTimeInMillis
parameter_list|()
block|{
return|return
name|startedTimeInMillis
return|;
block|}
DECL|method|setStartTime ()
specifier|public
name|void
name|setStartTime
parameter_list|()
block|{
name|startedTimeInMillis
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

