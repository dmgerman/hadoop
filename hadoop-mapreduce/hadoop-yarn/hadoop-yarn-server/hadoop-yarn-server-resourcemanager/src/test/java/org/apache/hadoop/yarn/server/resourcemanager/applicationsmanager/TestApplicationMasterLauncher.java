begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
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
name|resourcemanager
operator|.
name|applicationsmanager
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|ApplicationState
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
name|ApplicationSubmissionContext
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
name|event
operator|.
name|EventHandler
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|security
operator|.
name|ApplicationTokenSecretManager
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
name|security
operator|.
name|client
operator|.
name|ClientToAMSecretManager
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
name|resourcemanager
operator|.
name|RMContext
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
name|resourcemanager
operator|.
name|RMContextImpl
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
name|resourcemanager
operator|.
name|amlauncher
operator|.
name|AMLauncherEvent
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
name|resourcemanager
operator|.
name|amlauncher
operator|.
name|AMLauncherEventType
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
name|resourcemanager
operator|.
name|amlauncher
operator|.
name|ApplicationMasterLauncher
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|MemStore
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|StoreFactory
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|AMLivelinessMonitor
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptImpl
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
name|junit
operator|.
name|After
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

begin_comment
comment|/**  * Testing the applications manager launcher.  *  */
end_comment

begin_class
DECL|class|TestApplicationMasterLauncher
specifier|public
class|class
name|TestApplicationMasterLauncher
block|{
comment|//  private static final Log LOG = LogFactory.getLog(TestApplicationMasterLauncher.class);
comment|//  private static RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
comment|//  private ApplicationMasterLauncher amLauncher;
comment|//  private DummyEventHandler asmHandle;
comment|//  private final ApplicationTokenSecretManager applicationTokenSecretManager =
comment|//    new ApplicationTokenSecretManager();
comment|//  private final ClientToAMSecretManager clientToAMSecretManager =
comment|//    new ClientToAMSecretManager();
comment|//
comment|//  Object doneLaunching = new Object();
comment|//  AtomicInteger launched = new AtomicInteger();
comment|//  AtomicInteger cleanedUp = new AtomicInteger();
comment|//  private RMContext context = new RMContextImpl(new MemStore(), null, null,
comment|//      null);
comment|//
comment|//  private Configuration conf = new Configuration();
comment|//
comment|//  private class DummyEventHandler implements EventHandler<ApplicationEvent> {
comment|//    @Override
comment|//    public void handle(ApplicationEvent appEvent) {
comment|//      ApplicationEventType event = appEvent.getType();
comment|//      switch (event) {
comment|//      case FINISH:
comment|//        synchronized(doneLaunching) {
comment|//          doneLaunching.notify();
comment|//        }
comment|//        break;
comment|//
comment|//      default:
comment|//        break;
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  private class DummyLaunch implements Runnable {
comment|//    public void run() {
comment|//      launched.incrementAndGet();
comment|//    }
comment|//  }
comment|//
comment|//  private class DummyCleanUp implements Runnable {
comment|//    private EventHandler eventHandler;
comment|//
comment|//    public DummyCleanUp(EventHandler eventHandler) {
comment|//      this.eventHandler = eventHandler;
comment|//    }
comment|//    public void run() {
comment|//      cleanedUp.incrementAndGet();
comment|//      eventHandler.handle(new AMFinishEvent(null,
comment|//          ApplicationState.COMPLETED, "", ""));
comment|//    }
comment|//  }
comment|//
comment|//  private class DummyApplicationMasterLauncher extends
comment|//      ApplicationMasterLauncher {
comment|//    private EventHandler eventHandler;
comment|//
comment|//    public DummyApplicationMasterLauncher(
comment|//        ApplicationTokenSecretManager applicationTokenSecretManager,
comment|//        ClientToAMSecretManager clientToAMSecretManager,
comment|//        EventHandler eventHandler) {
comment|//      super(applicationTokenSecretManager, clientToAMSecretManager, context);
comment|//      this.eventHandler = eventHandler;
comment|//    }
comment|//
comment|//    @Override
comment|//    protected Runnable createRunnableLauncher(RMAppAttempt application,
comment|//        AMLauncherEventType event) {
comment|//      Runnable r = null;
comment|//      switch (event) {
comment|//      case LAUNCH:
comment|//        r = new DummyLaunch();
comment|//        break;
comment|//      case CLEANUP:
comment|//        r = new DummyCleanUp(eventHandler);
comment|//      default:
comment|//        break;
comment|//      }
comment|//      return r;
comment|//    }
comment|//  }
comment|//
comment|//  @Before
comment|//  public void setUp() {
comment|//    asmHandle = new DummyEventHandler();
comment|//    amLauncher = new DummyApplicationMasterLauncher(applicationTokenSecretManager,
comment|//        clientToAMSecretManager, asmHandle);
comment|//    context.getDispatcher().init(conf);
comment|//    amLauncher.init(conf);
comment|//    context.getDispatcher().start();
comment|//    amLauncher.start();
comment|//
comment|//  }
comment|//
comment|//  @After
comment|//  public void tearDown() {
comment|//    amLauncher.stop();
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testAMLauncher() throws Exception {
comment|//
comment|//    // Creat AppId
comment|//    ApplicationId appId = recordFactory
comment|//        .newRecordInstance(ApplicationId.class);
comment|//    appId.setClusterTimestamp(System.currentTimeMillis());
comment|//    appId.setId(1);
comment|//
comment|//    ApplicationAttemptId appAttemptId = Records
comment|//        .newRecord(ApplicationAttemptId.class);
comment|//    appAttemptId.setApplicationId(appId);
comment|//    appAttemptId.setAttemptId(1);
comment|//
comment|//    // Create submissionContext
comment|//    ApplicationSubmissionContext submissionContext = recordFactory
comment|//        .newRecordInstance(ApplicationSubmissionContext.class);
comment|//    submissionContext.setApplicationId(appId);
comment|//    submissionContext.setUser("dummyuser");
comment|//
comment|//    RMAppAttempt appAttempt = new RMAppAttemptImpl(appAttemptId,
comment|//        "dummyclienttoken", context, null, submissionContext);
comment|//
comment|//    // Tell AMLauncher to launch the appAttempt
comment|//    amLauncher.handle(new AMLauncherEvent(AMLauncherEventType.LAUNCH,
comment|//        appAttempt));
comment|//
comment|//    // Tell AMLauncher to cleanup the appAttempt
comment|//    amLauncher.handle(new AMLauncherEvent(AMLauncherEventType.CLEANUP,
comment|//        appAttempt));
comment|//
comment|//    synchronized (doneLaunching) {
comment|//      doneLaunching.wait(10000);
comment|//    }
comment|//    Assert.assertEquals(1, launched.get());
comment|//    Assert.assertEquals(1, cleanedUp.get());
comment|//  }
block|}
end_class

end_unit

