begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|YarnApplicationState
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
name|recovery
operator|.
name|RMStateStoreFactory
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

begin_class
DECL|class|TestASMStateMachine
specifier|public
class|class
name|TestASMStateMachine
block|{
comment|//  private static final Log LOG = LogFactory.getLog(TestASMStateMachine.class);
comment|//  private static RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
comment|//  RMContext context = new RMContextImpl(new MemStore());
comment|//  EventHandler handler;
comment|//  private boolean snreceivedCleanUp = false;
comment|//  private boolean snAllocateReceived = false;
comment|//  private boolean launchCalled = false;
comment|//  private boolean addedApplication = false;
comment|//  private boolean removedApplication = false;
comment|//  private boolean launchCleanupCalled = false;
comment|//  private AtomicInteger waitForState = new AtomicInteger();
comment|//  private Configuration conf = new Configuration();
comment|//  @Before
comment|//  public void setUp() {
comment|//    context.getDispatcher().init(conf);
comment|//    context.getDispatcher().start();
comment|//    handler = context.getDispatcher().getEventHandler();
comment|//    new DummyAMLaunchEventHandler();
comment|//    new DummySNEventHandler();
comment|//    new ApplicationTracker();
comment|//    new MockAppplicationMasterInfo();
comment|//  }
comment|//
comment|//  @After
comment|//  public void tearDown() {
comment|//
comment|//  }
comment|//
comment|//  private class DummyAMLaunchEventHandler implements EventHandler<ASMEvent<AMLauncherEventType>> {
comment|//    AppAttempt application;
comment|//    AtomicInteger amsync = new AtomicInteger(0);
comment|//
comment|//    public DummyAMLaunchEventHandler() {
comment|//      context.getDispatcher().register(AMLauncherEventType.class, this);
comment|//    }
comment|//
comment|//    @Override
comment|//    public void handle(ASMEvent<AMLauncherEventType> event) {
comment|//      switch(event.getType()) {
comment|//      case LAUNCH:
comment|//        launchCalled = true;
comment|//        application = event.getApplication();
comment|//        context.getDispatcher().getEventHandler().handle(
comment|//            new ApplicationEvent(ApplicationEventType.LAUNCHED,
comment|//                application.getApplicationID()));
comment|//        break;
comment|//      case CLEANUP:
comment|//        launchCleanupCalled = true;
comment|//        break;
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  private class DummySNEventHandler implements EventHandler<ASMEvent<SNEventType>> {
comment|//    AppAttempt application;
comment|//    AtomicInteger snsync = new AtomicInteger(0);
comment|//
comment|//    public DummySNEventHandler() {
comment|//      context.getDispatcher().register(SNEventType.class, this);
comment|//    }
comment|//
comment|//    @Override
comment|//    public void handle(ASMEvent<SNEventType> event) {
comment|//      switch(event.getType()) {
comment|//      case RELEASE:
comment|//        snreceivedCleanUp = true;
comment|//        break;
comment|//      case SCHEDULE:
comment|//        snAllocateReceived = true;
comment|//        application = event.getAppAttempt();
comment|//        context.getDispatcher().getEventHandler().handle(
comment|//            new AMAllocatedEvent(application.getApplicationID(),
comment|//                application.getMasterContainer()));
comment|//        break;
comment|//      }
comment|//    }
comment|//
comment|//  }
comment|//
comment|//  private class ApplicationTracker implements EventHandler<ASMEvent<ApplicationTrackerEventType>> {
comment|//    public ApplicationTracker() {
comment|//      context.getDispatcher().register(ApplicationTrackerEventType.class, this);
comment|//    }
comment|//
comment|//    @Override
comment|//    public void handle(ASMEvent<ApplicationTrackerEventType> event) {
comment|//      switch (event.getType()) {
comment|//      case ADD:
comment|//        addedApplication = true;
comment|//        break;
comment|//      case REMOVE:
comment|//        removedApplication = true;
comment|//        break;
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  private class MockAppplicationMasterInfo implements
comment|//      EventHandler<ApplicationEvent> {
comment|//
comment|//    MockAppplicationMasterInfo() {
comment|//      context.getDispatcher().register(ApplicationEventType.class, this);
comment|//    }
comment|//    @Override
comment|//    public void handle(ApplicationEvent event) {
comment|//      LOG.info("The event type is " + event.getType());
comment|//    }
comment|//  }
comment|//
comment|//  private void waitForState( ApplicationState
comment|//      finalState, AppAttemptImpl masterInfo) throws Exception {
comment|//    int count = 0;
comment|//    while(masterInfo.getState() != finalState&& count< 10) {
comment|//      Thread.sleep(500);
comment|//      count++;
comment|//    }
comment|//    Assert.assertEquals(finalState, masterInfo.getState());
comment|//  }
comment|//
comment|//  /* Test the state machine.
comment|//   *
comment|//   */
comment|//  @Test
comment|//  public void testStateMachine() throws Exception {
comment|//    ApplicationSubmissionContext submissioncontext = recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
comment|//    submissioncontext.setApplicationId(recordFactory.newRecordInstance(ApplicationId.class));
comment|//    submissioncontext.getApplicationId().setId(1);
comment|//    submissioncontext.getApplicationId().setClusterTimestamp(System.currentTimeMillis());
comment|//
comment|//    AppAttemptImpl masterInfo = new AppAttemptImpl(context,
comment|//        conf, "dummyuser", submissioncontext, "dummyToken", StoreFactory
comment|//            .createVoidAppStore(), new AMLivelinessMonitor(context
comment|//            .getDispatcher().getEventHandler()));
comment|//
comment|//    context.getDispatcher().register(ApplicationEventType.class, masterInfo);
comment|//    handler.handle(new ApplicationEvent(
comment|//        ApplicationEventType.ALLOCATE, submissioncontext.getApplicationId()));
comment|//
comment|//    waitForState(ApplicationState.LAUNCHED, masterInfo);
comment|//    Assert.assertTrue(snAllocateReceived);
comment|//    Assert.assertTrue(launchCalled);
comment|//    Assert.assertTrue(addedApplication);
comment|//    handler
comment|//        .handle(new AMRegistrationEvent(masterInfo.getMaster()));
comment|//    waitForState(ApplicationState.RUNNING, masterInfo);
comment|//    Assert.assertEquals(ApplicationState.RUNNING, masterInfo.getState());
comment|//
comment|//    ApplicationStatus status = recordFactory
comment|//        .newRecordInstance(ApplicationStatus.class);
comment|//    status.setApplicationId(masterInfo.getApplicationID());
comment|//    handler.handle(new AMStatusUpdateEvent(status));
comment|//
comment|//    /* check if the state is still RUNNING */
comment|//
comment|//    Assert.assertEquals(ApplicationState.RUNNING, masterInfo.getState());
comment|//
comment|//    handler.handle(new AMFinishEvent(masterInfo.getApplicationID(),
comment|//        ApplicationState.COMPLETED, "", ""));
comment|//    waitForState(ApplicationState.COMPLETED, masterInfo);
comment|//    Assert.assertEquals(ApplicationState.COMPLETED, masterInfo.getState());
comment|//    /* check if clean up is called for everyone */
comment|//    Assert.assertTrue(launchCleanupCalled);
comment|//    Assert.assertTrue(snreceivedCleanUp);
comment|//    Assert.assertTrue(removedApplication);
comment|//
comment|//    /* check if expiry doesnt make it failed */
comment|//    handler.handle(new ApplicationEvent(ApplicationEventType.EXPIRE,
comment|//        masterInfo.getApplicationID()));
comment|//    Assert.assertEquals(ApplicationState.COMPLETED, masterInfo.getState());
comment|//  }
block|}
end_class

end_unit

