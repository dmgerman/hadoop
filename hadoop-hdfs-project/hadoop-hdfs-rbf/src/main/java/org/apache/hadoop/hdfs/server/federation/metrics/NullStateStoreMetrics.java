begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
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
name|federation
operator|.
name|metrics
package|;
end_package

begin_comment
comment|/**  * Implementation of the State Store metrics which does not do anything.  * This is used when the metrics are disabled (e.g., tests).  */
end_comment

begin_class
DECL|class|NullStateStoreMetrics
specifier|public
class|class
name|NullStateStoreMetrics
extends|extends
name|StateStoreMetrics
block|{
DECL|method|addRead (long latency)
specifier|public
name|void
name|addRead
parameter_list|(
name|long
name|latency
parameter_list|)
block|{}
DECL|method|getReadOps ()
specifier|public
name|long
name|getReadOps
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getReadAvg ()
specifier|public
name|double
name|getReadAvg
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|addWrite (long latency)
specifier|public
name|void
name|addWrite
parameter_list|(
name|long
name|latency
parameter_list|)
block|{}
DECL|method|getWriteOps ()
specifier|public
name|long
name|getWriteOps
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getWriteAvg ()
specifier|public
name|double
name|getWriteAvg
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|addFailure (long latency)
specifier|public
name|void
name|addFailure
parameter_list|(
name|long
name|latency
parameter_list|)
block|{  }
DECL|method|getFailureOps ()
specifier|public
name|long
name|getFailureOps
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getFailureAvg ()
specifier|public
name|double
name|getFailureAvg
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|addRemove (long latency)
specifier|public
name|void
name|addRemove
parameter_list|(
name|long
name|latency
parameter_list|)
block|{}
DECL|method|getRemoveOps ()
specifier|public
name|long
name|getRemoveOps
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getRemoveAvg ()
specifier|public
name|double
name|getRemoveAvg
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|setCacheSize (String name, int size)
specifier|public
name|void
name|setCacheSize
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|size
parameter_list|)
block|{}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{}
block|}
end_class

end_unit

