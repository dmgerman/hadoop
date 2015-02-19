begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.timelineservice
package|package
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
name|timelineservice
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
name|classification
operator|.
name|InterfaceAudience
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
name|XmlElement
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
name|LinkedHashMap
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

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"metric"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|NONE
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TimelineMetric
specifier|public
class|class
name|TimelineMetric
block|{
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|info
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|singleData
specifier|private
name|Object
name|singleData
decl_stmt|;
DECL|field|timeSeries
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|Object
argument_list|>
name|timeSeries
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|endTime
specifier|private
name|long
name|endTime
decl_stmt|;
DECL|method|TimelineMetric ()
specifier|public
name|TimelineMetric
parameter_list|()
block|{    }
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"id"
argument_list|)
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|setId (String id)
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"info"
argument_list|)
DECL|method|getInfo ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
DECL|method|setInfo (Map<String, Object> info)
specifier|public
name|void
name|setInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
DECL|method|addInfo (Map<String, Object> info)
specifier|public
name|void
name|addInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|.
name|putAll
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|addInfo (String key, Object value)
specifier|public
name|void
name|addInfo
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|info
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"data"
argument_list|)
DECL|method|getSingleData ()
specifier|public
name|Object
name|getSingleData
parameter_list|()
block|{
return|return
name|singleData
return|;
block|}
DECL|method|setSingleData (Object singleData)
specifier|public
name|void
name|setSingleData
parameter_list|(
name|Object
name|singleData
parameter_list|)
block|{
name|this
operator|.
name|singleData
operator|=
name|singleData
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"timeseries"
argument_list|)
DECL|method|getTimeSeries ()
specifier|public
name|Map
argument_list|<
name|Long
argument_list|,
name|Object
argument_list|>
name|getTimeSeries
parameter_list|()
block|{
return|return
name|timeSeries
return|;
block|}
DECL|method|setTimeSeries (Map<Long, Object> timeSeries)
specifier|public
name|void
name|setTimeSeries
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|Object
argument_list|>
name|timeSeries
parameter_list|)
block|{
name|this
operator|.
name|timeSeries
operator|=
name|timeSeries
expr_stmt|;
block|}
DECL|method|addTimeSeries (Map<Long, Object> timeSeries)
specifier|public
name|void
name|addTimeSeries
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|Object
argument_list|>
name|timeSeries
parameter_list|)
block|{
name|this
operator|.
name|timeSeries
operator|.
name|putAll
argument_list|(
name|timeSeries
argument_list|)
expr_stmt|;
block|}
DECL|method|addTimeSeriesData (long timestamp, Object value)
specifier|public
name|void
name|addTimeSeriesData
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|timeSeries
operator|.
name|put
argument_list|(
name|timestamp
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"starttime"
argument_list|)
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
DECL|method|setStartTime (long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"endtime"
argument_list|)
DECL|method|getEndTime ()
specifier|public
name|long
name|getEndTime
parameter_list|()
block|{
return|return
name|endTime
return|;
block|}
DECL|method|setEndTime (long endTime)
specifier|public
name|void
name|setEndTime
parameter_list|(
name|long
name|endTime
parameter_list|)
block|{
name|this
operator|.
name|endTime
operator|=
name|endTime
expr_stmt|;
block|}
block|}
end_class

end_unit

