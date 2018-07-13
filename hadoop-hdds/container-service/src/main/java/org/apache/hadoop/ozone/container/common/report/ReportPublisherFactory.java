begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.report
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|report
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessage
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatusReportsProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
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
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory class to construct {@link ReportPublisher} for a report.  */
end_comment

begin_class
DECL|class|ReportPublisherFactory
specifier|public
class|class
name|ReportPublisherFactory
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
argument_list|,
DECL|field|report2publisher
name|Class
argument_list|<
name|?
extends|extends
name|ReportPublisher
argument_list|>
argument_list|>
name|report2publisher
decl_stmt|;
comment|/**    * Constructs {@link ReportPublisherFactory} instance.    *    * @param conf Configuration to be passed to the {@link ReportPublisher}    */
DECL|method|ReportPublisherFactory (Configuration conf)
specifier|public
name|ReportPublisherFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|report2publisher
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|report2publisher
operator|.
name|put
argument_list|(
name|NodeReportProto
operator|.
name|class
argument_list|,
name|NodeReportPublisher
operator|.
name|class
argument_list|)
expr_stmt|;
name|report2publisher
operator|.
name|put
argument_list|(
name|ContainerReportsProto
operator|.
name|class
argument_list|,
name|ContainerReportPublisher
operator|.
name|class
argument_list|)
expr_stmt|;
name|report2publisher
operator|.
name|put
argument_list|(
name|CommandStatusReportsProto
operator|.
name|class
argument_list|,
name|CommandStatusReportPublisher
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the ReportPublisher for the corresponding report.    *    * @param report report    *    * @return report publisher    */
DECL|method|getPublisherFor ( Class<? extends GeneratedMessage> report)
specifier|public
name|ReportPublisher
name|getPublisherFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
name|report
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|ReportPublisher
argument_list|>
name|publisherClass
init|=
name|report2publisher
operator|.
name|get
argument_list|(
name|report
argument_list|)
decl_stmt|;
if|if
condition|(
name|publisherClass
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No publisher found for report "
operator|+
name|report
argument_list|)
throw|;
block|}
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|publisherClass
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

