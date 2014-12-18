begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager.webapp
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
name|sharedcachemanager
operator|.
name|webapp
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Unstable
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
name|sharedcachemanager
operator|.
name|metrics
operator|.
name|CleanerMetrics
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
name|sharedcachemanager
operator|.
name|metrics
operator|.
name|ClientSCMMetrics
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
name|sharedcachemanager
operator|.
name|metrics
operator|.
name|SharedCacheUploaderMetrics
import|;
end_import

begin_comment
comment|/**  * This class is used to summarize useful shared cache manager metrics for the  * webUI display.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"SCMMetrics"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SCMMetricsInfo
specifier|public
class|class
name|SCMMetricsInfo
block|{
DECL|field|totalDeletedFiles
specifier|protected
name|long
name|totalDeletedFiles
decl_stmt|;
DECL|field|totalProcessedFiles
specifier|protected
name|long
name|totalProcessedFiles
decl_stmt|;
DECL|field|cacheHits
specifier|protected
name|long
name|cacheHits
decl_stmt|;
DECL|field|cacheMisses
specifier|protected
name|long
name|cacheMisses
decl_stmt|;
DECL|field|cacheReleases
specifier|protected
name|long
name|cacheReleases
decl_stmt|;
DECL|field|acceptedUploads
specifier|protected
name|long
name|acceptedUploads
decl_stmt|;
DECL|field|rejectedUploads
specifier|protected
name|long
name|rejectedUploads
decl_stmt|;
DECL|method|SCMMetricsInfo ()
specifier|public
name|SCMMetricsInfo
parameter_list|()
block|{   }
DECL|method|SCMMetricsInfo (CleanerMetrics cleanerMetrics, ClientSCMMetrics clientSCMMetrics, SharedCacheUploaderMetrics scmUploaderMetrics)
specifier|public
name|SCMMetricsInfo
parameter_list|(
name|CleanerMetrics
name|cleanerMetrics
parameter_list|,
name|ClientSCMMetrics
name|clientSCMMetrics
parameter_list|,
name|SharedCacheUploaderMetrics
name|scmUploaderMetrics
parameter_list|)
block|{
name|totalDeletedFiles
operator|=
name|cleanerMetrics
operator|.
name|getTotalDeletedFiles
argument_list|()
expr_stmt|;
name|totalProcessedFiles
operator|=
name|cleanerMetrics
operator|.
name|getTotalProcessedFiles
argument_list|()
expr_stmt|;
name|cacheHits
operator|=
name|clientSCMMetrics
operator|.
name|getCacheHits
argument_list|()
expr_stmt|;
name|cacheMisses
operator|=
name|clientSCMMetrics
operator|.
name|getCacheMisses
argument_list|()
expr_stmt|;
name|cacheReleases
operator|=
name|clientSCMMetrics
operator|.
name|getCacheReleases
argument_list|()
expr_stmt|;
name|acceptedUploads
operator|=
name|scmUploaderMetrics
operator|.
name|getAcceptedUploads
argument_list|()
expr_stmt|;
name|rejectedUploads
operator|=
name|scmUploaderMetrics
operator|.
name|getRejectUploads
argument_list|()
expr_stmt|;
block|}
DECL|method|getTotalDeletedFiles ()
specifier|public
name|long
name|getTotalDeletedFiles
parameter_list|()
block|{
return|return
name|totalDeletedFiles
return|;
block|}
DECL|method|getTotalProcessedFiles ()
specifier|public
name|long
name|getTotalProcessedFiles
parameter_list|()
block|{
return|return
name|totalProcessedFiles
return|;
block|}
DECL|method|getCacheHits ()
specifier|public
name|long
name|getCacheHits
parameter_list|()
block|{
return|return
name|cacheHits
return|;
block|}
DECL|method|getCacheMisses ()
specifier|public
name|long
name|getCacheMisses
parameter_list|()
block|{
return|return
name|cacheMisses
return|;
block|}
DECL|method|getCacheReleases ()
specifier|public
name|long
name|getCacheReleases
parameter_list|()
block|{
return|return
name|cacheReleases
return|;
block|}
DECL|method|getAcceptedUploads ()
specifier|public
name|long
name|getAcceptedUploads
parameter_list|()
block|{
return|return
name|acceptedUploads
return|;
block|}
DECL|method|getRejectUploads ()
specifier|public
name|long
name|getRejectUploads
parameter_list|()
block|{
return|return
name|rejectedUploads
return|;
block|}
block|}
end_class

end_unit

