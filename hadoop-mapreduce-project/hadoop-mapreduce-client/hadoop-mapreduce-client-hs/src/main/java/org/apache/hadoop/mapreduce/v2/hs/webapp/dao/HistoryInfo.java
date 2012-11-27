begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp.dao
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|JobHistoryServer
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
name|util
operator|.
name|VersionInfo
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|HistoryInfo
specifier|public
class|class
name|HistoryInfo
block|{
DECL|field|startedOn
specifier|protected
name|long
name|startedOn
decl_stmt|;
DECL|field|hadoopVersion
specifier|protected
name|String
name|hadoopVersion
decl_stmt|;
DECL|field|hadoopBuildVersion
specifier|protected
name|String
name|hadoopBuildVersion
decl_stmt|;
DECL|field|hadoopVersionBuiltOn
specifier|protected
name|String
name|hadoopVersionBuiltOn
decl_stmt|;
DECL|method|HistoryInfo ()
specifier|public
name|HistoryInfo
parameter_list|()
block|{
name|this
operator|.
name|startedOn
operator|=
name|JobHistoryServer
operator|.
name|historyServerTimeStamp
expr_stmt|;
name|this
operator|.
name|hadoopVersion
operator|=
name|VersionInfo
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|hadoopBuildVersion
operator|=
name|VersionInfo
operator|.
name|getBuildVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|hadoopVersionBuiltOn
operator|=
name|VersionInfo
operator|.
name|getDate
argument_list|()
expr_stmt|;
block|}
DECL|method|getHadoopVersion ()
specifier|public
name|String
name|getHadoopVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|hadoopVersion
return|;
block|}
DECL|method|getHadoopBuildVersion ()
specifier|public
name|String
name|getHadoopBuildVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|hadoopBuildVersion
return|;
block|}
DECL|method|getHadoopVersionBuiltOn ()
specifier|public
name|String
name|getHadoopVersionBuiltOn
parameter_list|()
block|{
return|return
name|this
operator|.
name|hadoopVersionBuiltOn
return|;
block|}
DECL|method|getStartedOn ()
specifier|public
name|long
name|getStartedOn
parameter_list|()
block|{
return|return
name|this
operator|.
name|startedOn
return|;
block|}
block|}
end_class

end_unit

