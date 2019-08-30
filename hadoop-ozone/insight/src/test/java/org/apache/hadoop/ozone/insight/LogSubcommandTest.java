begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.insight
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|insight
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Testing utility methods of the log subcommand test.  */
end_comment

begin_class
DECL|class|LogSubcommandTest
specifier|public
class|class
name|LogSubcommandTest
block|{
annotation|@
name|Test
DECL|method|filterLog ()
specifier|public
name|void
name|filterLog
parameter_list|()
block|{
name|LogSubcommand
name|logSubcommand
init|=
operator|new
name|LogSubcommand
argument_list|()
decl_stmt|;
name|String
name|result
init|=
name|logSubcommand
operator|.
name|processLogLine
argument_list|(
literal|"2019-08-04 12:27:08,648 [TRACE|org.apache.hadoop.hdds.scm.node"
operator|+
literal|".SCMNodeManager|SCMNodeManager] HB is received from "
operator|+
literal|"[datanode=localhost]:<json>storageReport {\\n  storageUuid: "
operator|+
literal|"\"DS-29204db6-a615-4106-9dd4-ce294c2f4cf6\"\\n  "
operator|+
literal|"storageLocation: \"/tmp/hadoop-elek/dfs/data\"\\n  capacity: "
operator|+
literal|"8348086272\\n  scmUsed: 4096\\n  remaining: 8246956032n  "
operator|+
literal|"storageType: DISK\\n  failed: falsen}\\n</json>\n"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

