begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.failmon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|failmon
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_comment
comment|/**********************************************************  * Objects of this class parse the output of ifconfig to   * gather information about present Network Interface Cards  * in the system. The list of NICs to poll is specified in the   * configuration file.  *   **********************************************************/
end_comment

begin_class
DECL|class|NICParser
specifier|public
class|class
name|NICParser
extends|extends
name|ShellParser
block|{
DECL|field|nics
name|String
index|[]
name|nics
decl_stmt|;
comment|/**    * Constructs a NICParser and reads the list of NICs to query    */
DECL|method|NICParser ()
specifier|public
name|NICParser
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|nics
operator|=
name|Environment
operator|.
name|getProperty
argument_list|(
literal|"nic.list"
argument_list|)
operator|.
name|split
argument_list|(
literal|",\\s*"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads and parses the output of ifconfig for a specified NIC and     * creates an appropriate EventRecord that holds the desirable     * information for it.    *     * @param device the NIC device name to query    *     * @return the EventRecord created    */
DECL|method|query (String device)
specifier|public
name|EventRecord
name|query
parameter_list|(
name|String
name|device
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|CharSequence
name|sb
init|=
name|Environment
operator|.
name|runCommandGeneric
argument_list|(
literal|"/sbin/ifconfig "
operator|+
name|device
argument_list|)
decl_stmt|;
name|EventRecord
name|retval
init|=
operator|new
name|EventRecord
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|,
name|InetAddress
operator|.
name|getAllByName
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|,
literal|"NIC"
argument_list|,
literal|"Unknown"
argument_list|,
name|device
argument_list|,
literal|"-"
argument_list|)
decl_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"hwAddress"
argument_list|,
name|findPattern
argument_list|(
literal|"HWaddr\\s*([\\S{2}:]{17})"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"ipAddress"
argument_list|,
name|findPattern
argument_list|(
literal|"inet\\s+addr:\\s*([\\w.?]*)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|tmp
init|=
name|findPattern
argument_list|(
literal|"inet\\s+addr:\\s*([\\w.?]*)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"status"
argument_list|,
operator|(
name|tmp
operator|==
literal|null
operator|)
condition|?
literal|"DOWN"
else|:
literal|"UP"
argument_list|)
expr_stmt|;
if|if
condition|(
name|tmp
operator|!=
literal|null
condition|)
name|retval
operator|.
name|set
argument_list|(
literal|"ipAddress"
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"rxPackets"
argument_list|,
name|findPattern
argument_list|(
literal|"RX\\s*packets\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"rxErrors"
argument_list|,
name|findPattern
argument_list|(
literal|"RX.+errors\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"rxDropped"
argument_list|,
name|findPattern
argument_list|(
literal|"RX.+dropped\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"rxOverruns"
argument_list|,
name|findPattern
argument_list|(
literal|"RX.+overruns\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"rxFrame"
argument_list|,
name|findPattern
argument_list|(
literal|"RX.+frame\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"txPackets"
argument_list|,
name|findPattern
argument_list|(
literal|"TX\\s*packets\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"txErrors"
argument_list|,
name|findPattern
argument_list|(
literal|"TX.+errors\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"txDropped"
argument_list|,
name|findPattern
argument_list|(
literal|"TX.+dropped\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"txOverruns"
argument_list|,
name|findPattern
argument_list|(
literal|"TX.+overruns\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"txCarrier"
argument_list|,
name|findPattern
argument_list|(
literal|"TX.+carrier\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"collisions"
argument_list|,
name|findPattern
argument_list|(
literal|"\\s+collisions\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"rxBytes"
argument_list|,
name|findPattern
argument_list|(
literal|"RX\\s*bytes\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
literal|"txBytes"
argument_list|,
name|findPattern
argument_list|(
literal|"TX\\s*bytes\\s*:\\s*(\\d+)"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|retval
return|;
block|}
comment|/**    * Invokes query() to do the parsing and handles parsing errors for     * each one of the NICs specified in the configuration.     *     * @return an array of EventRecords that holds one element that represents    * the current state of network interfaces.    */
DECL|method|monitor ()
specifier|public
name|EventRecord
index|[]
name|monitor
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|EventRecord
argument_list|>
name|recs
init|=
operator|new
name|ArrayList
argument_list|<
name|EventRecord
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nic
range|:
name|nics
control|)
block|{
try|try
block|{
name|recs
operator|.
name|add
argument_list|(
name|query
argument_list|(
name|nic
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|EventRecord
index|[]
name|T
init|=
operator|new
name|EventRecord
index|[
name|recs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|recs
operator|.
name|toArray
argument_list|(
name|T
argument_list|)
return|;
block|}
comment|/**    * Return a String with information about this class    *     * @return A String describing this class    */
DECL|method|getInfo ()
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
name|String
name|retval
init|=
literal|"ifconfig parser for interfaces: "
decl_stmt|;
for|for
control|(
name|String
name|nic
range|:
name|nics
control|)
name|retval
operator|+=
name|nic
operator|+
literal|" "
expr_stmt|;
return|return
name|retval
return|;
block|}
block|}
end_class

end_unit

