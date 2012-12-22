begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|jobhistory
operator|.
name|JHAdminConfig
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|conf
operator|.
name|YarnConfiguration
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
name|util
operator|.
name|BuilderUtils
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

begin_class
DECL|class|TestMapReduceTrackingUriPlugin
specifier|public
class|class
name|TestMapReduceTrackingUriPlugin
block|{
annotation|@
name|Test
DECL|method|testProducesHistoryServerUriForAppId ()
specifier|public
name|void
name|testProducesHistoryServerUriForAppId
parameter_list|()
throws|throws
name|URISyntaxException
block|{
specifier|final
name|String
name|historyAddress
init|=
literal|"example.net:424242"
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_WEBAPP_ADDRESS
argument_list|,
name|historyAddress
argument_list|)
expr_stmt|;
name|MapReduceTrackingUriPlugin
name|plugin
init|=
operator|new
name|MapReduceTrackingUriPlugin
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ApplicationId
name|id
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|String
name|jobSuffix
init|=
name|id
operator|.
name|toString
argument_list|()
operator|.
name|replaceFirst
argument_list|(
literal|"^application_"
argument_list|,
literal|"job_"
argument_list|)
decl_stmt|;
name|URI
name|expected
init|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|historyAddress
operator|+
literal|"/jobhistory/job/"
operator|+
name|jobSuffix
argument_list|)
decl_stmt|;
name|URI
name|actual
init|=
name|plugin
operator|.
name|getTrackingUri
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

