begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink.ganglia
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|sink
operator|.
name|ganglia
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This code supports Ganglia 3.1  *  */
end_comment

begin_class
DECL|class|GangliaSink31
specifier|public
class|class
name|GangliaSink31
extends|extends
name|GangliaSink30
block|{
DECL|field|LOG
specifier|public
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * The method sends metrics to Ganglia servers. The method has been taken from    * org.apache.hadoop.metrics.ganglia.GangliaContext31 with minimal changes in    * order to keep it in sync.    * @param groupName The group name of the metric    * @param name The metric name    * @param type The type of the metric    * @param value The value of the metric    * @param gConf The GangliaConf for this metric    * @param gSlope The slope for this metric    * @throws IOException    */
annotation|@
name|Override
DECL|method|emitMetric (String groupName, String name, String type, String value, GangliaConf gConf, GangliaSlope gSlope)
specifier|protected
name|void
name|emitMetric
parameter_list|(
name|String
name|groupName
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|value
parameter_list|,
name|GangliaConf
name|gConf
parameter_list|,
name|GangliaSlope
name|gSlope
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Metric was emitted with no name."
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Metric name "
operator|+
name|name
operator|+
literal|" was emitted with a null value."
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Metric name "
operator|+
name|name
operator|+
literal|", value "
operator|+
name|value
operator|+
literal|" has no type."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Emitting metric "
operator|+
name|name
operator|+
literal|", type "
operator|+
name|type
operator|+
literal|", value "
operator|+
name|value
operator|+
literal|", slope "
operator|+
name|gSlope
operator|.
name|name
argument_list|()
operator|+
literal|" from hostname "
operator|+
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// The following XDR recipe was done through a careful reading of
comment|// gm_protocol.x in Ganglia 3.1 and carefully examining the output of
comment|// the gmetric utility with strace.
comment|// First we send out a metadata message
name|xdr_int
argument_list|(
literal|128
argument_list|)
expr_stmt|;
comment|// metric_id = metadata_msg
name|xdr_string
argument_list|(
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
comment|// hostname
name|xdr_string
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// metric name
name|xdr_int
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// spoof = False
name|xdr_string
argument_list|(
name|type
argument_list|)
expr_stmt|;
comment|// metric type
name|xdr_string
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// metric name
name|xdr_string
argument_list|(
name|gConf
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
comment|// units
name|xdr_int
argument_list|(
name|gSlope
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
comment|// slope
name|xdr_int
argument_list|(
name|gConf
operator|.
name|getTmax
argument_list|()
argument_list|)
expr_stmt|;
comment|// tmax, the maximum time between metrics
name|xdr_int
argument_list|(
name|gConf
operator|.
name|getDmax
argument_list|()
argument_list|)
expr_stmt|;
comment|// dmax, the maximum data value
name|xdr_int
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|/*Num of the entries in extra_value field for                                    Ganglia 3.1.x*/
name|xdr_string
argument_list|(
literal|"GROUP"
argument_list|)
expr_stmt|;
comment|/*Group attribute*/
name|xdr_string
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
comment|/*Group value*/
comment|// send the metric to Ganglia hosts
name|emitToGangliaHosts
argument_list|()
expr_stmt|;
comment|// Now we send out a message with the actual value.
comment|// Technically, we only need to send out the metadata message once for
comment|// each metric, but I don't want to have to record which metrics we did and
comment|// did not send.
name|xdr_int
argument_list|(
literal|133
argument_list|)
expr_stmt|;
comment|// we are sending a string value
name|xdr_string
argument_list|(
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
comment|// hostName
name|xdr_string
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// metric name
name|xdr_int
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// spoof = False
name|xdr_string
argument_list|(
literal|"%s"
argument_list|)
expr_stmt|;
comment|// format field
name|xdr_string
argument_list|(
name|value
argument_list|)
expr_stmt|;
comment|// metric value
comment|// send the metric to Ganglia hosts
name|emitToGangliaHosts
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

