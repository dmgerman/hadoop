begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * TestGangliaContext.java  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.ganglia
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
operator|.
name|ganglia
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|metrics
operator|.
name|ContextFactory
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
name|metrics
operator|.
name|spi
operator|.
name|AbstractMetricsContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MulticastSocket
import|;
end_import

begin_class
annotation|@
name|Deprecated
DECL|class|TestGangliaContext
specifier|public
class|class
name|TestGangliaContext
block|{
annotation|@
name|Test
DECL|method|testShouldCreateDatagramSocketByDefault ()
specifier|public
name|void
name|testShouldCreateDatagramSocketByDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|GangliaContext
name|context
init|=
operator|new
name|GangliaContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|init
argument_list|(
literal|"gangliaContext"
argument_list|,
name|ContextFactory
operator|.
name|getFactory
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Created MulticastSocket"
argument_list|,
name|context
operator|.
name|datagramSocket
operator|instanceof
name|MulticastSocket
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShouldCreateDatagramSocketIfMulticastIsDisabled ()
specifier|public
name|void
name|testShouldCreateDatagramSocketIfMulticastIsDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|GangliaContext
name|context
init|=
operator|new
name|GangliaContext
argument_list|()
decl_stmt|;
name|ContextFactory
name|factory
init|=
name|ContextFactory
operator|.
name|getFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setAttribute
argument_list|(
literal|"gangliaContext.multicast"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|context
operator|.
name|init
argument_list|(
literal|"gangliaContext"
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Created MulticastSocket"
argument_list|,
name|context
operator|.
name|datagramSocket
operator|instanceof
name|MulticastSocket
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShouldCreateMulticastSocket ()
specifier|public
name|void
name|testShouldCreateMulticastSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|GangliaContext
name|context
init|=
operator|new
name|GangliaContext
argument_list|()
decl_stmt|;
name|ContextFactory
name|factory
init|=
name|ContextFactory
operator|.
name|getFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setAttribute
argument_list|(
literal|"gangliaContext.multicast"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|context
operator|.
name|init
argument_list|(
literal|"gangliaContext"
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Did not create MulticastSocket"
argument_list|,
name|context
operator|.
name|datagramSocket
operator|instanceof
name|MulticastSocket
argument_list|)
expr_stmt|;
name|MulticastSocket
name|multicastSocket
init|=
operator|(
name|MulticastSocket
operator|)
name|context
operator|.
name|datagramSocket
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Did not set default TTL"
argument_list|,
name|multicastSocket
operator|.
name|getTimeToLive
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShouldSetMulticastSocketTtl ()
specifier|public
name|void
name|testShouldSetMulticastSocketTtl
parameter_list|()
throws|throws
name|Exception
block|{
name|GangliaContext
name|context
init|=
operator|new
name|GangliaContext
argument_list|()
decl_stmt|;
name|ContextFactory
name|factory
init|=
name|ContextFactory
operator|.
name|getFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setAttribute
argument_list|(
literal|"gangliaContext.multicast"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setAttribute
argument_list|(
literal|"gangliaContext.multicast.ttl"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|context
operator|.
name|init
argument_list|(
literal|"gangliaContext"
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|MulticastSocket
name|multicastSocket
init|=
operator|(
name|MulticastSocket
operator|)
name|context
operator|.
name|datagramSocket
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Did not set TTL"
argument_list|,
name|multicastSocket
operator|.
name|getTimeToLive
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseShouldCloseTheSocketWhichIsCreatedByInit ()
specifier|public
name|void
name|testCloseShouldCloseTheSocketWhichIsCreatedByInit
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractMetricsContext
name|context
init|=
operator|new
name|GangliaContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|init
argument_list|(
literal|"gangliaContext"
argument_list|,
name|ContextFactory
operator|.
name|getFactory
argument_list|()
argument_list|)
expr_stmt|;
name|GangliaContext
name|gangliaContext
init|=
operator|(
name|GangliaContext
operator|)
name|context
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Socket already closed"
argument_list|,
name|gangliaContext
operator|.
name|datagramSocket
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Socket not closed"
argument_list|,
name|gangliaContext
operator|.
name|datagramSocket
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

