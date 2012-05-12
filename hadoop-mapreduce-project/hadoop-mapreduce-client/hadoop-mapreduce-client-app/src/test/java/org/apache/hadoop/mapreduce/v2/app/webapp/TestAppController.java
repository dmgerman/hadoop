begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp
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
name|app
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|util
operator|.
name|Records
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
name|webapp
operator|.
name|Controller
operator|.
name|RequestContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|TestAppController
specifier|public
class|class
name|TestAppController
block|{
DECL|field|appController
specifier|private
name|AppController
name|appController
decl_stmt|;
DECL|field|ctx
specifier|private
name|RequestContext
name|ctx
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|AppContext
name|context
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getApplicationID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|App
name|app
init|=
operator|new
name|App
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ctx
operator|=
name|mock
argument_list|(
name|RequestContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|appController
operator|=
operator|new
name|AppController
argument_list|(
name|app
argument_list|,
name|conf
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBadRequest ()
specifier|public
name|void
name|testBadRequest
parameter_list|()
block|{
name|String
name|message
init|=
literal|"test string"
decl_stmt|;
name|appController
operator|.
name|badRequest
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|verifyExpectations
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBadRequestWithNullMessage ()
specifier|public
name|void
name|testBadRequestWithNullMessage
parameter_list|()
block|{
comment|// It should not throw NullPointerException
name|appController
operator|.
name|badRequest
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|verifyExpectations
argument_list|(
name|StringUtils
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyExpectations (String message)
specifier|private
name|void
name|verifyExpectations
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|verify
argument_list|(
name|ctx
argument_list|)
operator|.
name|setStatus
argument_list|(
literal|400
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|ctx
argument_list|)
operator|.
name|set
argument_list|(
literal|"app.id"
argument_list|,
literal|"application_0_0000"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|ctx
argument_list|)
operator|.
name|set
argument_list|(
name|eq
argument_list|(
literal|"rm.web"
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|ctx
argument_list|)
operator|.
name|set
argument_list|(
literal|"title"
argument_list|,
literal|"Bad request: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

